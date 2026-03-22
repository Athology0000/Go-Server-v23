#include "AStarPlanner.h"
#include <unordered_map>
#include <queue>
#include <cmath>
#include <algorithm>

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
            c.aotv = 20.0f; c.etherwarp = 15.0f; break;
        case MovementProfile::COMBAT:
            c.aotv = 2.0f; c.etherwarp = 1.5f; c.sprint = 0.5f; break;
        case MovementProfile::GROUND_ONLY:
            c.aotv = 9999.f; c.etherwarp = 9999.f;
            c.jump = 9999.f; c.sprintJump = 9999.f; break;
        default: break;
    }
    return c;
}

float AStarPlanner::heuristic(const Vec3i& a, const Vec3i& b) {
    float dx = (float)(a.x - b.x), dy = (float)(a.y - b.y), dz = (float)(a.z - b.z);
    return std::sqrt(dx*dx + dy*dy + dz*dz);
}

struct PQNode {
    float f; Vec3i pos;
    bool operator>(const PQNode& o) const { return f > o.f; }
};

struct Vec3iHash {
    size_t operator()(const Vec3i& v) const {
        size_t h = (size_t)(v.x * 1000003) ^ (size_t)(v.y * 999983) ^ (size_t)(v.z * 999979);
        return h;
    }
};
struct Vec3iEq {
    bool operator()(const Vec3i& a, const Vec3i& b) const {
        return a.x==b.x && a.y==b.y && a.z==b.z;
    }
};

void AStarPlanner::startAsync(Vec3i start, Vec3i goal,
                               const WorldAccessor& world,
                               MovementProfile profile) {
    cancel();
    complete_.store(false);
    running_.store(true);
    cancelled_.store(false);

    MovementCosts costs = costsForProfile(profile);

    // Snapshot buffer — avoids data race when main thread updates WorldAccessor next tick
    std::vector<uint8_t> bufSnapshot;
    int snapBx = 0, snapBy = 0, snapBz = 0;
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
        // Thread-local WorldAccessor using snapshot — no shared state with main thread
        WorldAccessor localWorld;
        if (!bufSnapshot.empty())
            localWorld.setBuffer(bufSnapshot.data(), snapBx, snapBy, snapBz);
        MovementExpander expander(localWorld, costs);

        using Map = std::unordered_map<Vec3i, float,    Vec3iHash, Vec3iEq>;
        using PMap= std::unordered_map<Vec3i, Vec3i,    Vec3iHash, Vec3iEq>;
        using PQ  = std::priority_queue<PQNode, std::vector<PQNode>, std::greater<PQNode>>;

        Map gScore; PMap parent; PQ open;
        gScore[start] = 0;
        open.push({heuristic(start, goal), start});

        AStarResult res;
        int iters = 0;
        const int MAX_ITER = 150000;

        Vec3i bestFrontierNode = start;
        float bestFrontierDist = heuristic(start, goal);   // initial = distance from start
        bool  frontierAdvanced = false;

        while (!open.empty() && !cancelled_.load() && iters++ < MAX_ITER) {
            auto [f, cur] = open.top(); open.pop();

            if (!goalInBuffer) {
                float d = heuristic(cur, goal);
                if (d < bestFrontierDist) {
                    bestFrontierDist  = d;
                    bestFrontierNode  = cur;
                    frontierAdvanced  = true;
                }
            }

            if (cur.x==goal.x && cur.y==goal.y && cur.z==goal.z) {
                std::vector<Vec3i> path;
                Vec3i c = cur;
                while (!(c.x==start.x && c.y==start.y && c.z==start.z)) {
                    path.push_back(c);
                    c = parent[c];
                }
                path.push_back(start);
                std::reverse(path.begin(), path.end());
                res.found = true;
                res.nodes = std::move(path);
                break;
            }

            float gCur = gScore.count(cur) ? gScore[cur] : 1e9f;
            for (auto& nb : expander.expand(cur)) {
                float ng = gCur + nb.cost;
                if (!gScore.count(nb.pos) || ng < gScore[nb.pos]) {
                    gScore[nb.pos] = ng;
                    parent[nb.pos] = cur;
                    open.push({ng + heuristic(nb.pos, goal), nb.pos});
                }
            }
        }

        if (!res.found && !goalInBuffer && frontierAdvanced) {
            // Reconstruct path to best frontier node
            std::vector<Vec3i> path;
            Vec3i c = bestFrontierNode;
            while (!(c.x == start.x && c.y == start.y && c.z == start.z)) {
                path.push_back(c);
                auto it = parent.find(c);
                if (it == parent.end()) { path.clear(); break; }
                c = it->second;
            }
            if (!path.empty()) {
                path.push_back(start);
                std::reverse(path.begin(), path.end());
                res.found     = false;
                res.isPartial = true;
                res.nodes     = std::move(path);
            }
            // If path reconstruction failed (no parent chain), leave res.found=false, isPartial=false → FAILED
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
