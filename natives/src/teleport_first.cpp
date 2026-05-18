#include "teleport_first.hpp"

#include "common.hpp"

#include <algorithm>
#include <chrono>
#include <cmath>
#include <limits>
#include <queue>
#include <unordered_map>

namespace v5pf {

namespace {

constexpr double PI = 3.14159265358979323846;

// Hard safety caps — the planner runs on a background thread and MUST return
// quickly. Without these the macro waits forever for a plan that never lands.
constexpr int HARD_MAX_ITERATIONS = 18000;
constexpr int HARD_MAX_NODES = 9000;
constexpr int AOTV_OFFSET_MAX = 16;     // precompute table radius
constexpr int MAX_AOTV_NEIGHBOURS = 14; // per-node branching cap
constexpr int MAX_ETHER_NEIGHBOURS = 8;
// Each instant-transmission hop must cut at least this much distance to the
// goal, otherwise it is rejected. Forces a few long hops instead of many
// short ones (the "uses too many transmissions" complaint).
constexpr double MIN_AOTV_GAIN = 6.0;
// Etherwarp is a precision finishing move only: it is offered solely when the
// burrow is already within one etherwarp, and only hops that land essentially
// on the burrow are accepted.
constexpr double ETHER_FINISH_RADIUS = 4.0;

inline uint16_t flagsAt(const WorldSnapshot& w, int x, int y, int z) {
  return w.getFlags(x, y, z);
}
inline bool isPassable(const WorldSnapshot& w, int x, int y, int z) {
  return (flagsAt(w, x, y, z) & VF_PASSABLE) != 0;
}
inline bool isSolid(const WorldSnapshot& w, int x, int y, int z) {
  return (flagsAt(w, x, y, z) & VF_SOLID) != 0;
}
inline bool isTeleportClear(const WorldSnapshot& w, int x, int y, int z) {
  const uint16_t f = flagsAt(w, x, y, z);
  if ((f & VF_SOLID) != 0) return false;
  if ((f & VF_ETHER_TELEPORT_CLEAR) != 0) return true;
  return (f & VF_PASSABLE) != 0 && (f & VF_BLOCKING_WALL) == 0;
}
inline bool isSafeStand(const WorldSnapshot& w, int x, int y, int z) {
  return isPassable(w, x, y, z) &&
         isPassable(w, x, y + 1, z) &&
         isSolid(w, x, y - 1, z);
}

// Single-height teleport corridor sample. Branching is bounded now, so one
// torso-height ray per candidate is enough and keeps per-node cost low.
bool corridorClear(
  const WorldSnapshot& w,
  double ax, double ay, double az,
  double bx, double by, double bz
) {
  const double dx = bx - ax, dy = by - ay, dz = bz - az;
  const double len = std::sqrt(dx * dx + dy * dy + dz * dz);
  if (len < 1e-6) return true;
  const int steps = static_cast<int>(len / 0.5) + 1;
  const double inv = 1.0 / static_cast<double>(steps);
  for (int i = 1; i < steps; i++) {
    const double t = static_cast<double>(i) * inv;
    if (!isTeleportClear(w,
          static_cast<int>(std::floor(ax + dx * t)),
          static_cast<int>(std::floor(ay + dy * t)),
          static_cast<int>(std::floor(az + dz * t)))) {
      return false;
    }
  }
  return true;
}

bool teleportCorridorClear(
  const WorldSnapshot& w, const Int3& from, const Int3& to
) {
  return corridorClear(w,
            from.x + 0.5, from.y + 1.30, from.z + 0.5,
            to.x + 0.5,   to.y + 1.30,   to.z + 0.5);
}

bool settleByGravity(const WorldSnapshot& w, Int3 p, int maxDrop, Int3& out) {
  for (int d = 0; d <= maxDrop; d++) {
    if (isSafeStand(w, p.x, p.y, p.z)) { out = p; return true; }
    if (!isPassable(w, p.x, p.y, p.z) || !isPassable(w, p.x, p.y + 1, p.z)) {
      return false;
    }
    p.y -= 1;
  }
  return false;
}

inline double dist3D(const Int3& a, const Int3& b) {
  const double dx = a.x - b.x, dy = a.y - b.y, dz = a.z - b.z;
  return std::sqrt(dx * dx + dy * dy + dz * dz);
}

void anglesBetween(double ex, double ey, double ez,
                   double tx, double ty, double tz,
                   float& yaw, float& pitch) {
  const double dx = tx - ex, dy = ty - ey, dz = tz - ez;
  const double xz = std::sqrt(dx * dx + dz * dz);
  yaw = static_cast<float>(std::atan2(-dx, dz) * (180.0 / PI));
  pitch = static_cast<float>(-(std::atan2(dy, xz) * (180.0 / PI)));
}

struct AotvOffset { int dx, dy, dz; double dist; };

// Built once. Longest hops first so a forward scan naturally prefers covering
// the most distance per cast (fewer hops, less mana, faster traversal).
const std::vector<AotvOffset>& aotvOffsetTable() {
  static const std::vector<AotvOffset> table = [] {
    std::vector<AotvOffset> t;
    for (int dx = -AOTV_OFFSET_MAX; dx <= AOTV_OFFSET_MAX; dx += 2) {
      for (int dz = -AOTV_OFFSET_MAX; dz <= AOTV_OFFSET_MAX; dz += 2) {
        for (int dy = -6; dy <= 6; dy++) {
          const double d = std::sqrt(double(dx) * dx + double(dy) * dy +
                                     double(dz) * dz);
          if (d < 4.0 || d > AOTV_OFFSET_MAX) continue;
          t.push_back(AotvOffset{dx, dy, dz, d});
        }
      }
    }
    std::sort(t.begin(), t.end(),
              [](const AotvOffset& a, const AotvOffset& b) {
                return a.dist > b.dist;
              });
    return t;
  }();
  return table;
}

struct Edge {
  Int3 to;
  TfHopType type;
  int manaCost;
  double cost;
  float yaw;
  float pitch;
};

struct SearchNode {
  Int3 pos;
  int parent;
  TfHopType type;
  double g;
  double f;
  int manaSpent;
  float yaw;
  float pitch;
};

struct HeapItem {
  double f;
  int idx;
  bool operator>(const HeapItem& o) const { return f > o.f; }
};

void neighbours(
  const WorldSnapshot& w,
  const TeleportFirstParams& p,
  const Int3& from,
  const Int3& goal,
  std::vector<Edge>& out
) {
  out.clear();
  const double toGoal = dist3D(from, goal);
  const double gdx = goal.x - from.x;
  const double gdz = goal.z - from.z;
  const double gLen = std::sqrt(gdx * gdx + gdz * gdz);
  const double gDirX = gLen > 1e-6 ? gdx / gLen : 0.0;
  const double gDirZ = gLen > 1e-6 ? gdz / gLen : 0.0;

  const double ex = from.x + 0.5, ey = from.y + 1.62, ez = from.z + 0.5;
  int aotvCount = 0;
  int etherCount = 0;
  bool hasTeleportExit = false;

  // ---- AOTV (short, no sneak) ----
  if (p.aotvEnabled) {
    for (const AotvOffset& o : aotvOffsetTable()) {
      if (aotvCount >= MAX_AOTV_NEIGHBOURS) break;
      if (o.dist > p.transmissionRange) continue;

      // Forward-cone prune BEFORE any voxel work (the expensive part).
      const double oLen = std::sqrt(double(o.dx) * o.dx + double(o.dz) * o.dz);
      if (oLen > 1e-6 && gLen > 1e-6) {
        const double dot = (gDirX * o.dx + gDirZ * o.dz) / oLen;
        if (dot < -0.15) continue; // moving away from the goal
      }

      Int3 aim{from.x + o.dx, from.y + o.dy, from.z + o.dz};
      if (!isPassable(w, aim.x, aim.y, aim.z) ||
          !isPassable(w, aim.x, aim.y + 1, aim.z)) {
        continue;
      }
      Int3 landing;
      if (!settleByGravity(w, aim, 12, landing)) continue;
      if (!teleportCorridorClear(w, from, landing)) continue;

      const double landDist = dist3D(landing, goal);
      // Must make meaningful progress (or essentially reach the goal).
      if (landDist > p.goalReachedRadius &&
          toGoal - landDist < MIN_AOTV_GAIN) {
        continue;
      }

      float yaw, pitch;
      anglesBetween(ex, ey, ez,
                    landing.x + 0.5, landing.y + 0.9, landing.z + 0.5,
                    yaw, pitch);
      const double gravityPen = std::max(0, aim.y - landing.y) * 0.03;
      out.push_back(Edge{landing, TfHopType::AOTV, p.transmissionMana,
                         1.85 + gravityPen, yaw, pitch});
      aotvCount++;
      hasTeleportExit = true;
    }
  }

  // ---- Etherwarp (sneak) — finishing move ONLY. Offered solely when the
  // burrow is already within a single etherwarp, and (below) only hops that
  // land essentially on the burrow are kept. Instant transmission handles all
  // long-distance traversal. ----
  if (p.etherwarpEnabled && toGoal <= p.etherwarpRange) {
    for (int yawDeg = 0; yawDeg < 360 && etherCount < MAX_ETHER_NEIGHBOURS;
         yawDeg += 20) {
      const double yr = yawDeg * (PI / 180.0);
      const double rdx = -std::sin(yr);
      const double rdz = std::cos(yr);
      if (gLen > 1e-6 && (gDirX * rdx + gDirZ * rdz) < 0.0) continue;

      for (int pitchDeg = -25; pitchDeg <= 25; pitchDeg += 25) {
        if (etherCount >= MAX_ETHER_NEIGHBOURS) break;
        const double pr = pitchDeg * (PI / 180.0);
        const double cp = std::cos(pr);
        const double dirX = rdx * cp;
        const double dirY = -std::sin(pr);
        const double dirZ = rdz * cp;

        Int3 hit{0, 0, 0};
        bool found = false;
        const int maxSteps = static_cast<int>(p.etherwarpRange / 0.5) + 1;
        for (int s = 4; s <= maxSteps; s++) {
          const double t = s * 0.5;
          if (t > p.etherwarpRange) break;
          const int vx = static_cast<int>(std::floor(ex + dirX * t));
          const int vy = static_cast<int>(std::floor(ey + dirY * t));
          const int vz = static_cast<int>(std::floor(ez + dirZ * t));
          if (isSolid(w, vx, vy, vz)) { hit = Int3{vx, vy, vz}; found = true; break; }
          if (!isTeleportClear(w, vx, vy, vz)) break;
        }
        if (!found) continue;

        Int3 landing{hit.x, hit.y + 1, hit.z};
        if (!isSafeStand(w, landing.x, landing.y, landing.z)) continue;
        const double d = dist3D(from, landing);
        if (d < 4.0 || d > p.etherwarpRange) continue;
        const double landDist = dist3D(landing, goal);
        // Precision finish only — must land basically on the burrow.
        if (landDist > p.goalReachedRadius + ETHER_FINISH_RADIUS) continue;

        float yaw, pitch;
        anglesBetween(ex, ey, ez,
                      hit.x + 0.5, hit.y + 1.0, hit.z + 0.5, yaw, pitch);
        out.push_back(Edge{landing, TfHopType::ETHERWARP, p.etherwarpMana,
                           2.5, yaw, pitch});
        etherCount++;
        hasTeleportExit = true;
      }
    }
  }

  // ---- Walk: expensive when teleports exist, cheap bridge when boxed in ----
  const double walkCost = hasTeleportExit ? 8.0 : 1.5;
  static constexpr int WX[8] = {1, -1, 0, 0, 1, 1, -1, -1};
  static constexpr int WZ[8] = {0, 0, 1, -1, 1, -1, 1, -1};
  for (int i = 0; i < 8; i++) {
    for (int dy = -1; dy <= 1; dy++) {
      Int3 c{from.x + WX[i], from.y + dy, from.z + WZ[i]};
      if (!isSafeStand(w, c.x, c.y, c.z)) continue;
      if (WX[i] != 0 && WZ[i] != 0) {
        const bool sideX = isPassable(w, from.x + WX[i], from.y, from.z) &&
                           isPassable(w, from.x + WX[i], from.y + 1, from.z);
        const bool sideZ = isPassable(w, from.x, from.y, from.z + WZ[i]) &&
                           isPassable(w, from.x, from.y + 1, from.z + WZ[i]);
        if (!sideX || !sideZ) continue;
      }
      out.push_back(Edge{c, TfHopType::WALK, 0,
                         walkCost + (dy > 0 ? 0.15 : 0.0), 0.0f, 0.0f});
    }
  }
}

} // namespace

std::optional<TeleportFirstResult> findTeleportFirstPath(
  const WorldSnapshot& world,
  const TeleportFirstParams& params,
  std::atomic_bool& cancelFlag
) {
  const auto startTime = std::chrono::steady_clock::now();

  Int3 start = params.start;
  if (!isSafeStand(world, start.x, start.y, start.z)) {
    Int3 settled;
    if (settleByGravity(world, start, 6, settled)) start = settled;
  }
  const Int3 goal = params.goal;
  const double reachR = params.goalReachedRadius;

  const int maxIters = std::min(params.maxIterations, HARD_MAX_ITERATIONS);
  const int maxNodes = std::min(params.maxNodes, HARD_MAX_NODES);

  std::vector<SearchNode> nodes;
  nodes.reserve(static_cast<size_t>(maxNodes));
  std::unordered_map<uint64_t, int> visited;
  visited.reserve(static_cast<size_t>(maxNodes));

  // Greedy heuristic in cost units: ~one AOTV hop per transmissionRange of
  // remaining distance, weighted > 1 so A* beelines to the goal instead of
  // flood-filling (the bug that made the old planner never return).
  const double hopLen = std::max(4.0, params.transmissionRange);
  auto heuristic = [&](const Int3& n) {
    return dist3D(n, goal) / hopLen * 1.85 * 1.7;
  };

  nodes.push_back(SearchNode{
    start, -1, TfHopType::WALK, 0.0, heuristic(start), 0, 0.0f, 0.0f});
  visited.emplace(coordKey(start.x, start.y, start.z), 0);

  std::priority_queue<HeapItem, std::vector<HeapItem>, std::greater<HeapItem>> open;
  open.push(HeapItem{nodes[0].f, 0});

  int bestIdx = 0;
  double bestDist = dist3D(start, goal);
  int iterations = 0;
  std::vector<Edge> edges;

  while (!open.empty() && iterations < maxIters) {
    if (cancelFlag.load()) return std::nullopt;
    iterations++;

    const HeapItem top = open.top();
    open.pop();
    const int curIdx = top.idx;
    if (top.f > nodes[static_cast<size_t>(curIdx)].f + 1e-9) continue;

    const SearchNode cur = nodes[static_cast<size_t>(curIdx)];
    const double curDist = dist3D(cur.pos, goal);
    if (curDist < bestDist) { bestDist = curDist; bestIdx = curIdx; }
    if (curDist <= reachR) { bestIdx = curIdx; bestDist = curDist; break; }
    if (static_cast<int>(nodes.size()) >= maxNodes) continue;

    neighbours(world, params, cur.pos, goal, edges);
    for (const Edge& e : edges) {
      const int nextMana = cur.manaSpent + e.manaCost;
      if (e.type != TfHopType::WALK && params.availableMana > 0 &&
          nextMana > params.availableMana) {
        continue;
      }
      const double ng = cur.g + e.cost;
      const uint64_t key = coordKey(e.to.x, e.to.y, e.to.z);
      const auto it = visited.find(key);
      if (it != visited.end()) {
        const int ni = it->second;
        if (ng >= nodes[static_cast<size_t>(ni)].g) continue;
        SearchNode& n = nodes[static_cast<size_t>(ni)];
        n.g = ng;
        n.f = ng + heuristic(e.to);
        n.parent = curIdx;
        n.type = e.type;
        n.manaSpent = nextMana;
        n.yaw = e.yaw;
        n.pitch = e.pitch;
        open.push(HeapItem{n.f, ni});
      } else {
        const int ni = static_cast<int>(nodes.size());
        const double nf = ng + heuristic(e.to);
        nodes.push_back(SearchNode{
          e.to, curIdx, e.type, ng, nf, nextMana, e.yaw, e.pitch});
        visited.emplace(key, ni);
        open.push(HeapItem{nf, ni});
      }
    }
  }

  std::vector<int> chain;
  for (int i = bestIdx; i != -1; i = nodes[static_cast<size_t>(i)].parent) {
    chain.push_back(i);
  }
  std::reverse(chain.begin(), chain.end());

  TeleportFirstResult result;
  result.reachedGoal = bestDist <= reachR;
  result.points.reserve(chain.size());
  result.hopTypes.reserve(chain.size());
  result.yaw.reserve(chain.size());
  result.pitch.reserve(chain.size());
  for (const int idx : chain) {
    const SearchNode& n = nodes[static_cast<size_t>(idx)];
    result.points.push_back(n.pos);
    result.hopTypes.push_back(static_cast<int>(n.type));
    result.yaw.push_back(n.yaw);
    result.pitch.push_back(n.pitch);
  }

  const auto elapsedNs = std::chrono::duration_cast<std::chrono::nanoseconds>(
    std::chrono::steady_clock::now() - startTime).count();
  result.timeMs = elapsedNs / 1000000LL;
  result.nodesExplored = iterations;
  result.nanosecondsPerNode = iterations > 0
    ? static_cast<double>(elapsedNs) / static_cast<double>(iterations) : 0.0;

  // Return whatever progress we have (even the start node) so the caller can
  // fall back to a direct approach + replan instead of stalling forever.
  if (result.points.empty()) return std::nullopt;
  return result;
}

} // namespace v5pf
