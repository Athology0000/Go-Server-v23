#include "AStarPlanner.h"
#include <algorithm>
#include <cmath>
#include <queue>
#include <unordered_map>
#include <unordered_set>

AStarPlanner::~AStarPlanner() { cancel(); }

void AStarPlanner::cancel() {
    cancelled_.store(true);
    if (thread_.joinable()) thread_.join();
    cancelled_.store(false);
    running_.store(false);
}

MovementCosts AStarPlanner::costsForProfile(MovementProfile p) {
    MovementCosts c;
    switch (p) {
        case MovementProfile::MINING:
            c.aotv = 20.0f;
            c.etherwarp = 15.0f;
            break;
        case MovementProfile::COMBAT:
            c.aotv = 2.0f;
            c.etherwarp = 1.5f;
            c.sprint = 0.5f;
            break;
        case MovementProfile::GROUND_ONLY:
            c.aotv = 9999.f;
            c.etherwarp = 9999.f;
            c.jump = 9999.f;
            c.sprintJump = 9999.f;
            break;
        default:
            break;
    }
    return c;
}

float AStarPlanner::heuristic(const Vec3i& a, const Vec3i& b) {
    float dx = (float)(a.x - b.x);
    float dy = (float)(a.y - b.y);
    float dz = (float)(a.z - b.z);
    return std::sqrt(dx * dx + dy * dy + dz * dz);
}

struct PQNode {
    float f;
    Vec3i pos;

    bool operator>(const PQNode& other) const { return f > other.f; }
};

struct Vec3iHash {
    size_t operator()(const Vec3i& v) const {
        size_t h = (size_t)(v.x * 1000003) ^ (size_t)(v.y * 999983) ^ (size_t)(v.z * 999979);
        return h;
    }
};

struct Vec3iEq {
    bool operator()(const Vec3i& a, const Vec3i& b) const {
        return a.x == b.x && a.y == b.y && a.z == b.z;
    }
};

struct ParentInfo {
    Vec3i parent;
    ActionType action;
    float stepCost;
};

void AStarPlanner::startAsync(Vec3i start, Vec3i goal,
                               const WorldAccessor& world,
                               MovementProfile profile) {
    cancel();
    complete_.store(false);
    running_.store(true);
    cancelled_.store(false);

    MovementCosts costs = costsForProfile(profile);

    std::vector<uint8_t> bufSnapshot;
    int snapBx = 0;
    int snapBy = 0;
    int snapBz = 0;
    if (world.bufferData()) {
        bufSnapshot.assign(world.bufferData(), world.bufferData() + BUF_SIZE);
        snapBx = world.originX();
        snapBy = world.originY();
        snapBz = world.originZ();
    }

    bool goalInBuffer = world.inBuffer(goal.x, goal.y, goal.z);

    thread_ = std::thread([this, start, goal, costs, goalInBuffer,
                           bufSnapshot = std::move(bufSnapshot),
                           snapBx, snapBy, snapBz]() mutable {
        WorldAccessor localWorld;
        if (!bufSnapshot.empty()) {
            localWorld.setBuffer(bufSnapshot.data(), snapBx, snapBy, snapBz);
        }
        MovementExpander expander(localWorld, costs);

        using Map = std::unordered_map<Vec3i, float, Vec3iHash, Vec3iEq>;
        using PMap = std::unordered_map<Vec3i, ParentInfo, Vec3iHash, Vec3iEq>;
        using Set = std::unordered_set<Vec3i, Vec3iHash, Vec3iEq>;
        using PQ = std::priority_queue<PQNode, std::vector<PQNode>, std::greater<PQNode>>;

        Map gScore;
        PMap parent;
        Set closed;
        PQ open;
        gScore[start] = 0.0f;
        open.push({heuristic(start, goal), start});

        AStarResult res;
        int iters = 0;
        const int MAX_ITER = 150000;
        constexpr float STALE_F_EPS = 1e-4f;

        Vec3i bestFrontierNode = start;
        float bestFrontierDist = heuristic(start, goal);
        bool frontierAdvanced = false;

        auto samePos = [](const Vec3i& a, const Vec3i& b) {
            return a.x == b.x && a.y == b.y && a.z == b.z;
        };

        auto reconstructPath = [&](const Vec3i& end) {
            std::vector<PathNode> path;
            Vec3i current = end;
            while (!samePos(current, start)) {
                auto it = parent.find(current);
                if (it == parent.end()) {
                    path.clear();
                    return path;
                }
                path.push_back({current, it->second.action, it->second.stepCost});
                current = it->second.parent;
            }
            path.push_back({start, ActionType::WALK, 0.0f});
            std::reverse(path.begin(), path.end());
            return path;
        };

        while (!open.empty() && !cancelled_.load() && iters++ < MAX_ITER) {
            auto [f, cur] = open.top();
            open.pop();

            auto gIt = gScore.find(cur);
            if (gIt == gScore.end()) continue;

            float expectedF = gIt->second + heuristic(cur, goal);
            if (f > expectedF + STALE_F_EPS) continue;
            if (!closed.insert(cur).second) continue;

            if (!goalInBuffer) {
                float d = heuristic(cur, goal);
                if (d < bestFrontierDist) {
                    bestFrontierDist = d;
                    bestFrontierNode = cur;
                    frontierAdvanced = true;
                }
            }

            if (samePos(cur, goal)) {
                auto path = reconstructPath(cur);
                if (!path.empty()) {
                    res.found = true;
                    res.nodes = std::move(path);
                }
                break;
            }

            float gCur = gIt->second;
            for (const auto& nb : expander.expand(cur)) {
                if (closed.contains(nb.pos)) continue;

                float ng = gCur + nb.cost;
                auto nbIt = gScore.find(nb.pos);
                if (nbIt == gScore.end() || ng < nbIt->second) {
                    gScore[nb.pos] = ng;
                    parent[nb.pos] = {cur, nb.action, nb.cost};
                    open.push({ng + heuristic(nb.pos, goal), nb.pos});
                }
            }
        }

        if (!res.found && !goalInBuffer && frontierAdvanced) {
            auto path = reconstructPath(bestFrontierNode);
            if (!path.empty()) {
                res.isPartial = true;
                res.nodes = std::move(path);
            }
        }

        {
            std::lock_guard<std::mutex> lk(mutex_);
            result_ = std::move(res);
        }
        running_.store(false);
        complete_.store(true);
    });
}

AStarResult AStarPlanner::takeResult() {
    std::lock_guard<std::mutex> lk(mutex_);
    complete_.store(false);
    return std::move(result_);
}
