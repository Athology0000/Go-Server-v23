#include "MovementExpander.h"
#include <cmath>

static const int DX4[] = {1,-1,0,0};
static const int DZ4[] = {0,0,1,-1};

MovementExpander::MovementExpander(const WorldAccessor& w, const MovementCosts& c)
    : world_(w), costs_(c) {}

float MovementExpander::adjacentCost(const Vec3i& pos) const {
    float c = 0;
    for (int dx=-1; dx<=1; dx++) for (int dz=-1; dz<=1; dz++) {
        if (world_.isLava(pos.x+dx, pos.y, pos.z+dz)) c += costs_.lavaAdj;
        if (world_.isWater(pos.x+dx, pos.y, pos.z+dz)) c += costs_.waterSrc;
    }
    return c;
}

float MovementExpander::clearanceCost(const Vec3i& pos) const {
    int open = 0;
    for (int i = 0; i < 4; i++) {
        if (world_.getBlock(pos.x + DX4[i], pos.y, pos.z + DZ4[i]) == BT_AIR) open++;
    }
    // Penalty by lateral clearance: open air neighbors at same Y
    // open=0 dead-end: +0.7, open=1: +0.5, open=2: +0.3, open=3: +0.1, open=4 free: +0.0
    static const float penalty[5] = {0.7f, 0.5f, 0.3f, 0.1f, 0.0f};
    return penalty[open];
}

bool MovementExpander::clearanceOk(int x, int y, int z) const {
    return world_.isPassable(x, y, z) && world_.isPassable(x, y+1, z);
}

void MovementExpander::addWalkSprint(const Vec3i& from, std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
        if (!clearanceOk(nx, from.y, nz)) continue;
        bool lowCeiling = !world_.isPassable(nx, from.y + 2, nz);
        ActionType act = lowCeiling ? ActionType::WALK : ActionType::SPRINT;
        float base = lowCeiling ? costs_.walk : costs_.sprint;
        out.push_back({{nx, from.y, nz}, act, base + adjacentCost({nx, from.y, nz}) + clearanceCost({nx, from.y, nz})});
    }
}

void MovementExpander::addJumps(const Vec3i& from, std::vector<PathNode>& out) const {
    // Step up 1 block
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        int ny = from.y + 1;
        if (!world_.isSolid(nx, ny - 1, nz)) continue;
        if (!clearanceOk(nx, ny, nz)) continue;
        // clearanceCost intentionally omitted for jumps — landing geometry differs from corridor walking
        out.push_back({{nx, ny, nz}, ActionType::JUMP, costs_.jump + adjacentCost({nx, ny, nz})});
    }
    // Sprint-jump gap (2–4 blocks)
    for (int i = 0; i < 4; i++) {
        for (int dist = 2; dist <= 4; dist++) {
            int nx = from.x + DX4[i] * dist, nz = from.z + DZ4[i] * dist;
            if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
            if (!clearanceOk(nx, from.y, nz)) continue;
            bool gapClear = true;
            for (int d = 1; d < dist && gapClear; d++) {
                int mx = from.x + DX4[i]*d, mz = from.z + DZ4[i]*d;
                gapClear = world_.isPassable(mx, from.y, mz);
            }
            if (!gapClear) break;
            float c = (float)dist + costs_.sprintJump + adjacentCost({nx, from.y, nz});
            out.push_back({{nx, from.y, nz}, ActionType::SPRINT_JUMP, c});
        }
    }
}

void MovementExpander::addFall(const Vec3i& from, std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (!world_.isPassable(nx, from.y, nz)) continue;
        int ny = from.y - 1;
        while (ny > from.y - 24 && world_.isPassable(nx, ny, nz)) ny--;
        if (!world_.isSolid(nx, ny, nz)) continue;
        int landY = ny + 1;
        int drop = from.y - landY;
        if (drop <= 0 || drop > 23) continue;
        float c = drop * costs_.fall + adjacentCost({nx, landY, nz});
        // clearanceCost intentionally omitted for falls — landing clearance less critical for centering
        out.push_back({{nx, landY, nz}, ActionType::FALL, c});
    }
}

void MovementExpander::addLadder(const Vec3i& from, std::vector<PathNode>& out) const {
    if (world_.isLadder(from.x, from.y, from.z)) {
        if (world_.isLadder(from.x, from.y+1, from.z))
            out.push_back({{from.x, from.y+1, from.z}, ActionType::LADDER, costs_.ladder});
        if (world_.isLadder(from.x, from.y-1, from.z))
            out.push_back({{from.x, from.y-1, from.z}, ActionType::LADDER, costs_.ladder});
    }
}

void MovementExpander::addSwim(const Vec3i& from, std::vector<PathNode>& out) const {
    if (!world_.isWater(from.x, from.y, from.z)) return;
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        if (world_.isWater(nx, from.y, nz) || world_.isPassable(nx, from.y, nz))
            out.push_back({{nx, from.y, nz}, ActionType::WATER_SWIM, costs_.swim});
    }
    if (world_.isWater(from.x, from.y+1, from.z))
        out.push_back({{from.x, from.y+1, from.z}, ActionType::WATER_SWIM, costs_.swim});
}

void MovementExpander::addAOTV(const Vec3i& from, std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        for (int dist = 3; dist <= 11; dist++) {
            int nx = from.x + DX4[i]*dist, nz = from.z + DZ4[i]*dist;
            if (!world_.isPassable(nx, from.y, nz)) break;
            if (!world_.isWalkable(nx, from.y-1, nz)) continue;
            float c = costs_.aotv + dist * 0.05f;
            out.push_back({{nx, from.y, nz}, ActionType::AOTV, c});
        }
    }
}

void MovementExpander::addEtherwarp(const Vec3i& from, std::vector<PathNode>& out) const {
    for (int i = 0; i < 4; i++) {
        for (int dist = 5; dist <= 51; dist++) {
            int nx = from.x + DX4[i]*dist, nz = from.z + DZ4[i]*dist;
            if (world_.isSolid(nx, from.y, nz)) {
                if (clearanceOk(nx, from.y+1, nz))
                    out.push_back({{nx, from.y+1, nz}, ActionType::ETHERWARP, costs_.etherwarp});
                break;
            }
        }
    }
}

std::vector<PathNode> MovementExpander::expand(const Vec3i& from) const {
    std::vector<PathNode> out;
    out.reserve(32);
    addWalkSprint(from, out);
    addJumps(from, out);
    addFall(from, out);
    addLadder(from, out);
    addSwim(from, out);
    addAOTV(from, out);
    addEtherwarp(from, out);
    return out;
}
