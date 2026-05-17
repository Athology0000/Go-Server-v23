#pragma once

namespace v5pf::detail {

inline bool Runtime::moveTraverse(const Int3& current, const int dx, const int dz, MoveOut& out) {
  const int destX = current.x + dx;
  const int destZ = current.z + dz;
  if (!isSafe(destX, current.y, destZ)) return false;

  // Same-level walk: if snow is on either support, the sub-block height
  // difference must be within the auto-step rule, else this is not an edge.
  if (isSnow(current.x, current.y - 1, current.z) || isSnow(destX, current.y - 1, destZ)) {
    if (!snowStepAllowed(
          current.x, current.y - 1, current.z,
          destX, current.y - 1, destZ)) {
      return false;
    }
  }

  out.pos = {destX, current.y, destZ};
  out.cost = ActionCosts::SPRINT_ONE_BLOCK_TIME +
    pathPenalty(destX, current.y, destZ) +
    fluidPenalty(destX, current.y, destZ);
  return true;
}

inline bool Runtime::moveDiagonal(const Int3& current, const int dx, const int dz, MoveOut& out) {
  const int destX = current.x + dx;
  const int destZ = current.z + dz;

  if (!isSafe(destX, current.y, destZ)) return false;

  if (!isPassable(destX, current.y, current.z)) return false;
  if (!isPassable(current.x, current.y, destZ)) return false;
  if (!isPassable(destX, current.y + 1, current.z)) return false;
  if (!isPassable(current.x, current.y + 1, destZ)) return false;

  if (isSnow(current.x, current.y - 1, current.z) || isSnow(destX, current.y - 1, destZ)) {
    if (!snowStepAllowed(
          current.x, current.y - 1, current.z,
          destX, current.y - 1, destZ)) {
      return false;
    }
  }

  out.pos = {destX, current.y, destZ};
  out.cost = ActionCosts::SPRINT_DIAGONAL_TIME +
    pathPenalty(destX, current.y, destZ) +
    fluidPenalty(destX, current.y, destZ);
  return true;
}

inline bool Runtime::moveAscend(const Int3& current, const int dx, const int dz, MoveOut& out) {
  const int destX = current.x + dx;
  const int destZ = current.z + dz;

  if (!isPassable(current.x, current.y + 2, current.z)) return false;

  if (!isSolid(destX, current.y, destZ)) return false;
  if (isFenceLike(destX, current.y, destZ)) return false;

  if (!isPassable(destX, current.y + 1, destZ)) return false;
  if (!isPassable(destX, current.y + 2, destZ)) return false;

  const bool srcBottom = isBottomSlab(current.x, current.y - 1, current.z);
  const bool destBottom = isBottomSlab(destX, current.y, destZ);

  if (srcBottom && !destBottom) return false;

  const bool srcStair = isStairsBottom(current.x, current.y - 1, current.z);
  const bool destStair = isStairsBottom(destX, current.y, destZ);
  if (destStair && !isStairAscentDirection(destX, current.y, destZ, dx, dz)) return false;

  const bool snowInvolved =
    isSnow(destX, current.y, destZ) || isSnow(current.x, current.y - 1, current.z);
  if (snowInvolved) {
    // Snow has its own first-class rule, never the slab/BT_STEP path: an
    // in-rule snow rise auto-steps (no jump); out-of-rule snow is not an edge
    // so the planner routes around it or fails cleanly.
    if (!snowStepAllowed(
          current.x, current.y - 1, current.z,
          destX, current.y, destZ)) {
      return false;
    }
  }

  out.pos = {destX, current.y + 1, destZ};
  double baseCost = ActionCosts::JUMP_UP_ONE_BLOCK_TIME;
  if (snowInvolved) {
    baseCost = ActionCosts::SNOW_ASCENT_TIME;
  } else if (destBottom || srcStair || destStair) {
    baseCost = ActionCosts::SLAB_ASCENT_TIME;
  }

  out.cost = baseCost +
    pathPenalty(destX, current.y + 1, destZ) +
    fluidPenalty(destX, current.y + 1, destZ);
  return true;
}

inline bool Runtime::moveDescend(const Int3& current, const int dx, const int dz, MoveOut& out) {
  const int destX = current.x + dx;
  const int destZ = current.z + dz;

  if (!isPassable(destX, current.y + 1, destZ)) return false;
  if (!isPassable(destX, current.y, destZ)) return false;
  if (!isPassable(destX, current.y - 1, destZ)) return false;

  constexpr int maxFallHeight = 20;

  for (int dropBlocks = 1; dropBlocks <= maxFallHeight; dropBlocks++) {
    const int floorY = current.y - dropBlocks - 1;

    if (isPassable(destX, floorY, destZ)) {
      continue;
    }

    if (!isSolid(destX, floorY, destZ)) {
      return false;
    }

    const int destY = floorY + 1;
    if (!isPassable(destX, destY, destZ)) return false;
    if (!isPassable(destX, destY + 1, destZ)) return false;

    double totalCost = ActionCosts::WALK_OFF_EDGE_TIME + costs_.getFallTime(dropBlocks);
    if (dropBlocks > 3) {
      const int excess = dropBlocks - 3;
      totalCost += static_cast<double>(excess * excess) * 2.0;
    }

    totalCost += pathPenalty(destX, destY, destZ) + fluidPenalty(destX, destY, destZ);

    out.pos = {destX, destY, destZ};
    out.cost = totalCost;
    return true;
  }

  return false;
}

} // namespace v5pf::detail
