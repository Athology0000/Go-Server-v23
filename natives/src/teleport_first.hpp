#pragma once

#include "world_state.hpp"

#include <atomic>
#include <optional>
#include <vector>

namespace v5pf {

// Per-node action emitted by the teleport-first planner. Mirrors the Kotlin
// ActionType ordinals used for teleport firing (WALK / AOTV / ETHERWARP).
enum class TfHopType : int {
  WALK = 0,
  AOTV = 1,
  ETHERWARP = 2,
};

struct TeleportFirstParams {
  Int3 start{0, 0, 0};
  Int3 goal{0, 0, 0};
  double goalReachedRadius = 1.8;

  // Ranges in blocks, supplied by Kotlin from the live AOTV item attributes.
  double transmissionRange = 12.0;  // AOTV instant transmission (no sneak)
  double etherwarpRange = 57.0;     // etherwarp (sneak + use)

  // Mana budget. <= 0 means "unknown / unlimited" — plan without a mana cap.
  int availableMana = -1;
  int transmissionMana = 27;
  int etherwarpMana = 108;

  bool aotvEnabled = true;
  bool etherwarpEnabled = true;

  int maxIterations = 120000;
  int maxNodes = 60000;
};

struct TeleportFirstResult {
  std::vector<Int3> points;          // node positions (landing of each step)
  std::vector<int> hopTypes;         // TfHopType per node (parallel to points)
  std::vector<float> yaw;            // cast yaw per node (only meaningful for teleports)
  std::vector<float> pitch;          // cast pitch per node
  bool reachedGoal = false;
  long long timeMs = 0;
  int nodesExplored = 0;
  double nanosecondsPerNode = 0.0;
};

// Teleport-first hybrid planner: teleport hops (AOTV short / etherwarp long)
// are cheap graph edges, walking is an expensive bridge used only to cross
// obstacles that no teleport can clear. Returns the best path found (the path
// that reaches the goal, or the closest partial path) so the caller can keep
// advancing and replan from the new position.
std::optional<TeleportFirstResult> findTeleportFirstPath(
  const WorldSnapshot& world,
  const TeleportFirstParams& params,
  std::atomic_bool& cancelFlag
);

} // namespace v5pf
