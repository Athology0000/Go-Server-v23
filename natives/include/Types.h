#pragma once
#include <cstdint>

enum class PathStatus : int {
    IDLE = 0, PLANNING = 1, EXECUTING = 2,
    RECOVERING = 3, REPLANNING = 4, ARRIVED = 5, FAILED = 6
};

enum class ActionType : int {
    WALK = 0, SPRINT = 1, JUMP = 2, SPRINT_JUMP = 3,
    FALL = 4, LADDER = 5, WATER_SWIM = 6, AOTV = 7, ETHERWARP = 8
};

enum class MovementProfile : int {
    DEFAULT = 0, MINING = 1, COMBAT = 2, GROUND_ONLY = 3
};

struct Vec3d { double x, y, z; };
struct Vec3i { int x, y, z; };

inline bool isOnBlock(const Vec3d& point, const Vec3d& playerPos, double range) noexcept {
    return playerPos.x >= point.x - range && playerPos.x < point.x + range &&
           playerPos.z >= point.z - range && playerPos.z < point.z + range;
}

struct PathCommand {
    bool forward, back, jump, sneak, sprint;
    float targetYaw, targetPitch;   // raw angles — no GCD applied
    PathStatus status;
    ActionType activeAction;
    float distanceToTarget;         // 3D distance to current waypoint target
};

// World buffer constants
static constexpr int BUF_W = 96, BUF_H = 40, BUF_D = 96;
static constexpr int BUF_SIZE = BUF_W * BUF_H * BUF_D;
static constexpr int BUF_STRIDE_Z = BUF_W;           // z-stride: skip one row of X
static constexpr int BUF_STRIDE_Y = BUF_W * BUF_D;  // y-stride: skip one XZ plane

// Block type bytes
static constexpr uint8_t BT_AIR    = 0;
static constexpr uint8_t BT_SOLID  = 1;
static constexpr uint8_t BT_WATER  = 2;
static constexpr uint8_t BT_LAVA   = 3;
static constexpr uint8_t BT_LADDER = 4;
// Half-height blocks (slabs 0.5, thin snow, etc.) that Minecraft auto-steps onto.
// Treated as solid ground but do not require an explicit jump to ascend.
static constexpr uint8_t BT_STEP   = 5;
// Bottom stairs with the direction the player must move to climb them.
// Stairs retain direction so the planner does not walk into their blocked side faces.
static constexpr uint8_t BT_STAIR_NORTH = 6;
static constexpr uint8_t BT_STAIR_SOUTH = 7;
static constexpr uint8_t BT_STAIR_WEST  = 8;
static constexpr uint8_t BT_STAIR_EAST  = 9;
// Top stairs or unsupported stair shapes: solid for collision/standing, not a step-up edge.
static constexpr uint8_t BT_STAIR_BLOCKED = 10;

// Availability flags for AOTV / Etherwarp — passed from Kotlin each tick.
static constexpr int AVAIL_AOTV      = 1 << 0;
static constexpr int AVAIL_ETHERWARP = 1 << 1;
