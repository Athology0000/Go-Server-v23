#include "path_annotations.hpp"

#include "path_directional_scan.hpp"
#include "path_voxel_checks.hpp"

namespace v5pf {
namespace {

constexpr int FLAG_FLUID_FEET = 1 << 0;
constexpr int FLAG_FLUID_HEAD = 1 << 1;
constexpr int FLAG_LOW_HEADROOM = 1 << 2;
constexpr int FLAG_NEAR_EDGE = 1 << 3;
constexpr int FLAG_NEAR_WALL = 1 << 4;
constexpr int FLAG_STEP_UP_NEXT = 1 << 5;
constexpr int FLAG_DROP_NEXT = 1 << 6;
constexpr int FLAG_TIGHT_CORRIDOR = 1 << 7;
// Next node is an in-rule snow auto-step: the executor must NOT jump here,
// it should let vanilla step-assist carry the player up the snow.
constexpr int FLAG_SNOW_STEP = 1 << 8;

// Top surface / standing height of a support voxel, in eighths of a block
// (one snow layer = one eighth = two pixels). Mirrors native SnowGeometry.
inline int supportTopEighths(const WorldSnapshot& world, const int x, const int y, const int z) {
  const uint16_t f = flagsAt(world, x, y, z);
  const bool solid = hasVoxelFlag(f, VF_SOLID);
  if (hasVoxelFlag(f, VF_SNOW)) {
    const int layers = static_cast<int>(world.getSnow(x, y, z));
    if (layers > 0 && layers < 8) return layers;
  }
  return solid ? SnowGeometry::BLOCK_EIGHTHS : 0;
}

inline int supportStandEighths(const WorldSnapshot& world, const int x, const int y, const int z) {
  const uint16_t f = flagsAt(world, x, y, z);
  const bool solid = hasVoxelFlag(f, VF_SOLID);
  if (hasVoxelFlag(f, VF_SNOW)) {
    const int layers = static_cast<int>(world.getSnow(x, y, z));
    if (layers > 0 && layers < 8) return layers - 1;
  }
  return solid ? SnowGeometry::BLOCK_EIGHTHS : 0;
}

inline int edgeDistance(const WorldSnapshot& world, const int x, const int y, const int z) {
  return directionalDistance(world, x, y, z, isEdgeVoxel);
}

inline int wallDistance(const WorldSnapshot& world, const int x, const int y, const int z) {
  return directionalDistance(world, x, y, z, isWallVoxel);
}

} // namespace

std::vector<int> encodeNodeFlags(
  const WorldSnapshot& world,
  const std::vector<Int3>& nodes,
  const bool isFly,
  const bool includeProximity
) {
  if (nodes.empty()) return {};

  std::vector<int> out(nodes.size(), 0);
  const int yOffset = isFly ? 0 : -1;

  for (size_t i = 0; i < nodes.size(); i++) {
    const Int3& p = nodes[i];
    const int yOut = p.y + yOffset;
    const int walkY = yOut + (isFly ? 0 : 1);

    int flags = 0;
    if (isFluidVoxel(world, p.x, walkY, p.z)) flags |= FLAG_FLUID_FEET;
    if (isFluidVoxel(world, p.x, walkY + 1, p.z)) flags |= FLAG_FLUID_HEAD;

    if (!isFly) {
      if (!isWalkPassableVoxel(world, p.x, walkY + 2, p.z)) {
        flags |= FLAG_LOW_HEADROOM;
      }
    }

    if (includeProximity) {
      const int edgeDist = edgeDistance(world, p.x, walkY, p.z);
      const int wallDist = wallDistance(world, p.x, walkY, p.z);
      if (edgeDist <= 1) flags |= FLAG_NEAR_EDGE;
      if (wallDist <= 1) flags |= FLAG_NEAR_WALL;
      if (edgeDist <= 1 && wallDist <= 1) flags |= FLAG_TIGHT_CORRIDOR;
    }

    if (i + 1 < nodes.size()) {
      const Int3& n = nodes[i + 1];
      const int dy = n.y - p.y;
      if (dy > 0) flags |= FLAG_STEP_UP_NEXT;
      if (dy < 0) flags |= FLAG_DROP_NEXT;

      if (!isFly) {
        // Support is the voxel below the feet node (walkY-1 == p.y-1 here).
        const int curSupY = p.y - 1;
        const int nextSupY = n.y - 1;
        const bool snowInvolved =
          hasVoxelFlag(flagsAt(world, p.x, curSupY, p.z), VF_SNOW) ||
          hasVoxelFlag(flagsAt(world, n.x, nextSupY, n.z), VF_SNOW);
        if (snowInvolved) {
          const int curStand =
            curSupY * SnowGeometry::BLOCK_EIGHTHS +
            supportStandEighths(world, p.x, curSupY, p.z);
          const int nextTop =
            nextSupY * SnowGeometry::BLOCK_EIGHTHS +
            supportTopEighths(world, n.x, nextSupY, n.z);
          const int rise = nextTop - curStand;
          if (rise > 0 && rise <= SnowGeometry::MAX_STEP_EIGHTHS) {
            flags |= FLAG_SNOW_STEP;
          }
        }
      }
    }

    out[i] = flags;
  }

  return out;
}

std::vector<int> encodeKeyMetrics(const WorldSnapshot& world, const std::vector<Int3>& nodes) {
  if (nodes.empty()) return {};

  std::vector<int> out(nodes.size() * 3, 0);
  size_t idx = 0;
  for (size_t i = 0; i < nodes.size(); i++) {
    const Int3& p = nodes[i];
    out[idx++] = edgeDistance(world, p.x, p.y, p.z);
    out[idx++] = wallDistance(world, p.x, p.y, p.z);
    out[idx++] = i + 1 < nodes.size() ? nodes[i + 1].y - p.y : 0;
  }
  return out;
}

} // namespace v5pf
