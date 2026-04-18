#include "MovementExpander.h"
#include <algorithm>
#include <cmath>

static const int DX4[] = {1,-1,0,0};
static const int DZ4[] = {0,0,1,-1};
static const int DX_DIAGONAL[] = {1, 1, -1, -1};
static const int DZ_DIAGONAL[] = {1, -1, 1, -1};
static constexpr float DIAGONAL_COST_SCALE = 1.41421356f;
static constexpr float LOW_CEILING_PENALTY = 0.10f;
static constexpr float ADJACENT_WALL_PENALTY = 0.24f;
static constexpr float ADJACENT_CORNER_PENALTY = 0.16f;
static constexpr float SECOND_RING_WALL_PENALTY = 0.08f;
static constexpr float SECOND_RING_CORNER_PENALTY = 0.045f;
static constexpr float TIGHT_PASSAGE_PENALTY = 0.18f;
static constexpr float TIGHT_CORNER_PENALTY = 0.12f;
static constexpr float DIAGONAL_SIDE_CLEARANCE_SCALE = 0.65f;

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
    float penalty = 0.0f;

    for (int headOffset = 0; headOffset <= 1; headOffset++) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (dx == 0 && dz == 0) continue;

                if (!hasBlockingCollision(pos.x + dx, pos.y + headOffset, pos.z + dz)) continue;

                const int absX = std::abs(dx);
                const int absZ = std::abs(dz);
                const int chebyshev = absX > absZ ? absX : absZ;
                const bool axial = absX == 0 || absZ == 0;

                float basePenalty = SECOND_RING_CORNER_PENALTY;
                if (chebyshev <= 1 && axial) {
                    basePenalty = ADJACENT_WALL_PENALTY;
                } else if (chebyshev <= 1) {
                    basePenalty = ADJACENT_CORNER_PENALTY;
                } else if (axial) {
                    basePenalty = SECOND_RING_WALL_PENALTY;
                }

                penalty += headOffset == 0 ? basePenalty : basePenalty * 0.85f;
            }
        }
    }

    for (int dy = 2; dy <= 3; dy++) {
        if (hasBlockingCollision(pos.x, pos.y + dy, pos.z)) {
            penalty += LOW_CEILING_PENALTY / static_cast<float>(dy - 1);
        }
    }

    const bool blockPosX =
        hasBlockingCollision(pos.x + 1, pos.y, pos.z) || hasBlockingCollision(pos.x + 1, pos.y + 1, pos.z);
    const bool blockNegX =
        hasBlockingCollision(pos.x - 1, pos.y, pos.z) || hasBlockingCollision(pos.x - 1, pos.y + 1, pos.z);
    const bool blockPosZ =
        hasBlockingCollision(pos.x, pos.y, pos.z + 1) || hasBlockingCollision(pos.x, pos.y + 1, pos.z + 1);
    const bool blockNegZ =
        hasBlockingCollision(pos.x, pos.y, pos.z - 1) || hasBlockingCollision(pos.x, pos.y + 1, pos.z - 1);

    if ((blockPosX && blockNegX) || (blockPosZ && blockNegZ)) {
        penalty += TIGHT_PASSAGE_PENALTY;
    }
    if ((blockPosX || blockNegX) && (blockPosZ || blockNegZ)) {
        penalty += TIGHT_CORNER_PENALTY;
    }

    return penalty;
}

bool MovementExpander::clearanceOk(int x, int y, int z) const {
    return world_.isPassable(x, y, z) && world_.isPassable(x, y+1, z);
}

bool MovementExpander::hasBlockingCollision(int x, int y, int z) const {
    return world_.isSolid(x, y, z);
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
    // Micro step-up: walk/sprint onto half-height blocks (slabs etc.) without jumping.
    // Minecraft auto-steps blocks up to 0.5 high; no explicit jump input is needed.
    for (int i = 0; i < 4; i++) {
        int nx = from.x + DX4[i], nz = from.z + DZ4[i];
        int ny = from.y + 1;
        if (world_.getBlock(nx, from.y, nz) != BT_STEP) continue;
        if (!clearanceOk(nx, ny, nz)) continue;
        bool lowCeiling = !world_.isPassable(nx, ny + 2, nz);
        ActionType act = lowCeiling ? ActionType::WALK : ActionType::SPRINT;
        float base = lowCeiling ? costs_.walk : costs_.sprint;
        out.push_back({{nx, ny, nz}, act, base + adjacentCost({nx, ny, nz}) + clearanceCost({nx, ny, nz})});
    }

    for (int i = 0; i < 4; i++) {
        int stepX = DX_DIAGONAL[i];
        int stepZ = DZ_DIAGONAL[i];
        int nx = from.x + stepX;
        int nz = from.z + stepZ;

        if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
        if (!clearanceOk(nx, from.y, nz)) continue;

        int sideAX = from.x + stepX;
        int sideAZ = from.z;
        int sideBX = from.x;
        int sideBZ = from.z + stepZ;
        if (!world_.isWalkable(sideAX, from.y - 1, sideAZ) || !clearanceOk(sideAX, from.y, sideAZ)) continue;
        if (!world_.isWalkable(sideBX, from.y - 1, sideBZ) || !clearanceOk(sideBX, from.y, sideBZ)) continue;

        bool lowCeiling = !world_.isPassable(nx, from.y + 2, nz);
        ActionType act = lowCeiling ? ActionType::WALK : ActionType::SPRINT;
        float base = (lowCeiling ? costs_.walk : costs_.sprint) * DIAGONAL_COST_SCALE;
        const Vec3i destination{nx, from.y, nz};
        const Vec3i sideA{sideAX, from.y, sideAZ};
        const Vec3i sideB{sideBX, from.y, sideBZ};
        const float destinationClearance = clearanceCost(destination);
        const float diagonalWallPenalty =
            std::max(clearanceCost(sideA), clearanceCost(sideB)) * DIAGONAL_SIDE_CLEARANCE_SCALE;
        out.push_back({destination, act, base + adjacentCost(destination) + destinationClearance + diagonalWallPenalty});
    }
}

void MovementExpander::addJumps(const Vec3i& from, std::vector<PathNode>& out) const {
    // Player head sweeps through from.y+2 during any jump arc — no jumps under a ceiling.
    const bool launchClear = world_.isPassable(from.x, from.y + 2, from.z);

    // Step up 1 block — only for full-height solid blocks (BT_SOLID).
    // BT_STEP (slabs) are handled as walk/sprint step-ups in addWalkSprint.
    if (launchClear) {
        for (int i = 0; i < 4; i++) {
            int nx = from.x + DX4[i], nz = from.z + DZ4[i];
            int ny = from.y + 1;
            if (world_.getBlock(nx, ny - 1, nz) != BT_SOLID) continue;
            if (!clearanceOk(nx, ny, nz)) continue;
            // clearanceCost intentionally omitted for jumps - landing geometry differs from corridor walking
            out.push_back({{nx, ny, nz}, ActionType::JUMP, costs_.jump + adjacentCost({nx, ny, nz})});
        }
    }

    // Sprint-jump gap (2-4 blocks) — also requires launch clearance.
    if (!launchClear) return;
    for (int i = 0; i < 4; i++) {
        for (int dist = 2; dist <= 4; dist++) {
            int nx = from.x + DX4[i] * dist, nz = from.z + DZ4[i] * dist;
            if (!world_.isWalkable(nx, from.y - 1, nz)) continue;
            if (!clearanceOk(nx, from.y, nz)) continue;
            bool gapClear = true;
            for (int d = 1; d < dist && gapClear; d++) {
                int mx = from.x + DX4[i]*d, mz = from.z + DZ4[i]*d;
                // Feet may pass over lava mid-arc (player is airborne — lava-cost penalty still applies
                // at landing via adjacentCost). Head column must still be fully clear.
                const bool feetClear = world_.isPassable(mx, from.y, mz) || world_.isLava(mx, from.y, mz);
                gapClear = feetClear && world_.isPassable(mx, from.y + 1, mz);
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
        // clearanceCost intentionally omitted for falls - landing clearance less critical for centering
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
    // Native consumers only have universal execution for locomotion edges.
    // Keep teleport actions out of the planner until they are handled by every caller.
    return out;
}
