#include "teleport_first.hpp"

#include "common.hpp"

#include <algorithm>
#include <chrono>
#include <cmath>
#include <limits>
#include <queue>
#include <unordered_map>
#include <unordered_set>

namespace v5pf {

namespace {

constexpr double PI = 3.14159265358979323846;

// Hard safety caps — the planner runs on a background thread and MUST return
// quickly. Without these the macro waits forever for a plan that never lands.
constexpr int HARD_MAX_ITERATIONS = 18000;
constexpr int HARD_MAX_NODES = 9000;
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

// ---- Cooperative walk + instant-transmission tuning ----
// Realistic per-block walk time cost. The old planner forced this to 8.0
// whenever any teleport edge existed, which suppressed walking to near-zero
// and stopped the two from cooperating. With a true cost A* blends them: a
// single ~range-long AOTV hop (~1.85) beats walking that distance, but short
// gaps / obstacle threading / the final approach are cheaper walked.
constexpr double WALK_STEP_COST = 1.0;
constexpr double WALK_UPHILL_ADD = 0.15;
// Inside this radius of the goal we prefer a precise walk onto the burrow
// over an overshooting teleport: AOTV forward-cone leniency is dropped.
constexpr double FINISH_WALK_RADIUS = 12.0;

// ---- Climb-and-fly tuning ----
// Blocks of clearance kept above the tallest solid in the start->goal
// corridor when flying across at altitude.
constexpr int FLY_CLEARANCE = 5;
// Corridor terrain scan: horizontal sample spacing (blocks).
constexpr double FLY_SCAN_STEP = 2.0;
// Self-contained fly state, computed once per plan and threaded read-only
// into the node generator + heuristic. Private to this translation unit.
struct FlyContext {
  bool enabled = false;
  int flyY = 0;     // target cruise altitude (block Y)
  Int3 goal{0, 0, 0};
};

// AOTV look-direction sampling for the new max-reach node generator.
constexpr int AOTV_YAW_STEP_DEG = 18;
// Pitch rows, degrees (negative = upward). Extra steep-up rows feed the
// climb leg of the fly route; the rest cover diving / level traverse.
constexpr int AOTV_PITCH_ROWS[] = {-80, -68, -54, -40, -26, -12, 0, 14, 30, 48};

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

// Tallest solid block Y in a column, within the snapshot's vertical extent.
// Returns INT_MIN when the column is empty (no terrain to clear).
int columnTopSolid(const WorldSnapshot& w, int x, int z) {
  for (int y = w.maxY - 1; y >= w.minY; y--) {
    if (isSolid(w, x, y, z)) return y;
  }
  return std::numeric_limits<int>::min();
}

// Decide whether this plan flies, and at what altitude. The cruise height
// clears the tallest solid sampled along the start->goal XZ corridor, never
// dips below either endpoint, and is clamped into the snapshot. Self-contained
// — nothing outside this file needs to know fly mode exists.
FlyContext computeFlyContext(
  const WorldSnapshot& w, const TeleportFirstParams& p,
  const Int3& start, const Int3& goal
) {
  FlyContext fc;
  fc.goal = goal;
  if (p.flyTriggerDistance <= 0.0) return fc;

  const double hdx = goal.x - start.x, hdz = goal.z - start.z;
  const double horiz = std::sqrt(hdx * hdx + hdz * hdz);
  if (horiz <= p.flyTriggerDistance) return fc;

  int topSolid = std::max(start.y, goal.y);
  const int samples =
    std::max(2, static_cast<int>(horiz / FLY_SCAN_STEP) + 1);
  for (int i = 0; i <= samples; i++) {
    const double t = static_cast<double>(i) / static_cast<double>(samples);
    const int sx = static_cast<int>(std::floor(start.x + hdx * t));
    const int sz = static_cast<int>(std::floor(start.z + hdz * t));
    const int top = columnTopSolid(w, sx, sz);
    if (top != std::numeric_limits<int>::min() && top > topSolid) {
      topSolid = top;
    }
  }

  fc.enabled = true;
  fc.flyY = topSolid + FLY_CLEARANCE;
  fc.flyY = std::max(fc.flyY, std::max(start.y, goal.y));
  fc.flyY = std::min(fc.flyY, w.maxY - 2);
  return fc;
}

struct Edge {
  Int3 to;
  TfHopType type;
  int manaCost;
  double cost;
  float yaw;
  float pitch;
  bool airborne; // landing is mid-air (AOTV sky chain), no ground under it
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
  bool airborne;
};

struct HeapItem {
  double f;
  int idx;
  bool operator>(const HeapItem& o) const { return f > o.f; }
};

// New instant-transmission node generator.
//
// Instant transmission needs NO target block — it carries you as far along
// your exact look vector as that vector stays clear, up to AOTV range. So
// instead of snapping to a fixed offset-sphere table, each candidate is a
// look DIRECTION that we ray-march to its real maximum clear reach. That
// models the mechanic faithfully (open lanes → long hops, tight lanes →
// short hops) and is what makes a stringed upward chain "fly": with no
// ground under the aim point the landing is airborne and the next hop is
// another transmission.
void appendAotvNodes(
  const WorldSnapshot& w,
  const TeleportFirstParams& p,
  const FlyContext& fc,
  const Int3& from,
  const Int3& goal,
  double gDirX, double gDirZ, double gLen, double toGoal,
  bool inFinish,
  std::vector<Edge>& out
) {
  const double ex = from.x + 0.5, ey = from.y + 1.62, ez = from.z + 0.5;
  const bool climbing = fc.enabled && from.y < fc.flyY;

  // Build the look-direction set: a yaw x pitch sweep plus forced directions
  // (straight at the goal, and a steep climb toward the goal while flying)
  // so the key moves are always offered regardless of angular sampling.
  struct Dir { double x, y, z; };
  std::vector<Dir> dirs;
  dirs.reserve(64);

  // Forced: dead-on toward the goal in true 3D.
  {
    const double dx = goal.x - from.x, dy = goal.y - from.y,
                 dz = goal.z - from.z;
    const double l = std::sqrt(dx * dx + dy * dy + dz * dz);
    if (l > 1e-6) dirs.push_back(Dir{dx / l, dy / l, dz / l});
  }
  // Forced: steep climb along the goal's heading — the fly route's lift leg.
  if (climbing && gLen > 1e-6) {
    for (const double pdeg : {-72.0, -55.0}) {
      const double pr = pdeg * (PI / 180.0);
      const double cp = std::cos(pr);
      dirs.push_back(Dir{gDirX * cp, -std::sin(pr), gDirZ * cp});
    }
  }
  // Sweep.
  for (int yawDeg = 0; yawDeg < 360; yawDeg += AOTV_YAW_STEP_DEG) {
    const double yr = yawDeg * (PI / 180.0);
    const double hx = -std::sin(yr), hz = std::cos(yr);
    for (const int pd : AOTV_PITCH_ROWS) {
      const double pr = pd * (PI / 180.0);
      const double cp = std::cos(pr);
      dirs.push_back(Dir{hx * cp, -std::sin(pr), hz * cp});
    }
  }

  int aotvCount = 0;
  std::unordered_set<uint64_t> landed;
  const int maxSteps = static_cast<int>(p.transmissionRange / 0.5) + 1;

  for (const Dir& d : dirs) {
    if (aotvCount >= MAX_AOTV_NEIGHBOURS) break;

    // Forward-cone prune before the expensive march. Tightened near the
    // burrow so the finish is walked precisely rather than overshot.
    const double dHoriz = std::sqrt(d.x * d.x + d.z * d.z);
    if (dHoriz > 1e-6 && gLen > 1e-6) {
      const double dot = (gDirX * d.x + gDirZ * d.z) / dHoriz;
      const double minDot = inFinish ? 0.35 : -0.15;
      // While climbing, near-vertical lift hops barely move horizontally —
      // exempt them from the cone so the chain can gain altitude.
      const bool liftExempt = climbing && d.y < -0.55;
      if (dot < minDot && !liftExempt) continue;
    }

    // March the look vector to its farthest fully-clear voxel (feet + head).
    int bestStep = -1;
    for (int s = 1; s <= maxSteps; s++) {
      const double t = s * 0.5;
      if (t > p.transmissionRange) break;
      const int vx = static_cast<int>(std::floor(ex + d.x * t));
      const int vy = static_cast<int>(std::floor(ey + d.y * t));
      const int vz = static_cast<int>(std::floor(ez + d.z * t));
      if (!isTeleportClear(w, vx, vy, vz) ||
          !isTeleportClear(w, vx, vy + 1, vz)) {
        break;
      }
      if (t >= 4.0) bestStep = s;
    }
    if (bestStep < 0) continue; // nothing reachable >= 4 blocks this way

    const double bt = bestStep * 0.5;
    Int3 aim{static_cast<int>(std::floor(ex + d.x * bt)),
             static_cast<int>(std::floor(ey + d.y * bt)),
             static_cast<int>(std::floor(ez + d.z * bt))};
    if (!isPassable(w, aim.x, aim.y, aim.z) ||
        !isPassable(w, aim.x, aim.y + 1, aim.z)) {
      continue;
    }

    // Where the player ends up: settle to ground if there is some within a
    // long drop, otherwise the hop is airborne (a sky chain) and the next
    // hop must be another instant transmission.
    Int3 landing;
    bool airborne = false;
    if (!settleByGravity(w, aim, 28, landing)) {
      landing = aim;
      airborne = true;
    }

    const uint64_t lkey = coordKey(landing.x, landing.y, landing.z);
    if (!landed.insert(lkey).second) continue;

    const double landDist = dist3D(landing, goal);
    // Ground hops must make real forward progress (avoids tiny shuffle hops).
    // Airborne maneuvering hops (climb, arc over terrain) are exempt — they
    // often don't reduce distance yet but enable the route. While flying,
    // any airborne hop toward cruise altitude is also exempt.
    const bool progressExempt =
      airborne || (climbing && landing.y > from.y);
    if (!progressExempt && landDist > p.goalReachedRadius &&
        toGoal - landDist < MIN_AOTV_GAIN) {
      continue;
    }
    // Never accept a hop that moves substantially AWAY from the goal —
    // unless it is a lift hop trading horizontal distance for altitude.
    const bool liftAway = climbing && landing.y > from.y;
    if (!liftAway && landDist > toGoal + 4.0 &&
        landDist > p.goalReachedRadius) {
      continue;
    }

    float yaw, pitch;
    anglesBetween(ex, ey, ez,
                  aim.x + 0.5, aim.y + 1.0, aim.z + 0.5, yaw, pitch);
    const double gravityPen = std::max(0, aim.y - landing.y) * 0.03;
    // Fly-aware air penalty: a normal sky chain is mildly discouraged, but
    // an altitude-gaining hop while below cruise height is encouraged (~0),
    // and dropping well below cruise before nearing the goal is penalised.
    double airPen;
    if (!airborne) {
      airPen = 0.0;
    } else if (fc.enabled) {
      if (landing.y >= from.y && from.y < fc.flyY) {
        airPen = 0.0;                       // lift leg
      } else if (landing.y < fc.flyY - FLY_CLEARANCE && !inFinish) {
        airPen = 0.9;                       // sinking out of the lane early
      } else {
        airPen = 0.15;                      // level cruise
      }
    } else {
      airPen = 0.6;
    }
    out.push_back(Edge{landing, TfHopType::AOTV, p.transmissionMana,
                       1.85 + gravityPen + airPen, yaw, pitch, airborne});
    aotvCount++;
  }
}

void neighbours(
  const WorldSnapshot& w,
  const TeleportFirstParams& p,
  const FlyContext& fc,
  const Int3& from,
  const Int3& goal,
  bool fromAirborne,
  std::vector<Edge>& out
) {
  out.clear();
  const double toGoal = dist3D(from, goal);
  const double gdx = goal.x - from.x;
  const double gdz = goal.z - from.z;
  const double gLen = std::sqrt(gdx * gdx + gdz * gdz);
  const double gDirX = gLen > 1e-6 ? gdx / gLen : 0.0;
  const double gDirZ = gLen > 1e-6 ? gdz / gLen : 0.0;
  const bool inFinish = toGoal <= FINISH_WALK_RADIUS;

  const double ex = from.x + 0.5, ey = from.y + 1.62, ez = from.z + 0.5;
  int etherCount = 0;
  const size_t aotvStart = out.size();

  // ---- AOTV (short, no sneak) — new max-reach ray-march generator ----
  if (p.aotvEnabled) {
    appendAotvNodes(w, p, fc, from, goal, gDirX, gDirZ, gLen, toGoal,
                    inFinish, out);
  }
  const bool hasAotvExit = out.size() > aotvStart;

  // ---- Etherwarp (sneak) — finishing move ONLY. Offered solely when the
  // burrow is already within a single etherwarp, and (below) only hops that
  // land essentially on the burrow are kept. Instant transmission handles all
  // long-distance traversal. ----
  if (!fromAirborne && p.etherwarpEnabled && toGoal <= p.etherwarpRange) {
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
                           2.5, yaw, pitch, false});
        etherCount++;
      }
    }
  }

  // ---- Walk: a true per-block time cost so it cooperates with teleports
  // instead of being suppressed. A* now mixes them by real cost — long gaps
  // fly, short gaps / threading / the precise finish are walked. Not
  // available from an airborne node (you're falling, not standing). ----
  (void)hasAotvExit;
  const double walkCost = WALK_STEP_COST;
  static constexpr int WX[8] = {1, -1, 0, 0, 1, 1, -1, -1};
  static constexpr int WZ[8] = {0, 0, 1, -1, 1, -1, 1, -1};
  for (int i = 0; !fromAirborne && i < 8; i++) {
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
                         walkCost + (dy > 0 ? WALK_UPHILL_ADD : 0.0),
                         0.0f, 0.0f, false});
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

  // Decide once whether this plan flies, and at what cruise altitude.
  const FlyContext fly = computeFlyContext(world, params, start, goal);

  // Greedy heuristic in cost units: ~one AOTV hop per transmissionRange of
  // remaining distance, weighted > 1 so A* beelines to the goal instead of
  // flood-filling (the bug that made the old planner never return). In fly
  // mode the estimate is the three-leg route — climb to cruise, traverse at
  // cruise, descend onto the burrow — so the upward airborne chain registers
  // as real progress instead of looking like wasted distance and being
  // pruned away (the reason a naive search never climbs).
  const double hopLen = std::max(4.0, params.transmissionRange);
  auto heuristic = [&](const Int3& n) {
    double remaining;
    if (fly.enabled) {
      const double hdx = goal.x - n.x, hdz = goal.z - n.z;
      const double horiz = std::sqrt(hdx * hdx + hdz * hdz);
      const double climb = n.y < fly.flyY ? (fly.flyY - n.y) : 0.0;
      const double descent = std::max(0, fly.flyY - goal.y);
      remaining = climb + horiz + descent;
    } else {
      remaining = dist3D(n, goal);
    }
    return remaining / hopLen * 1.85 * 1.7;
  };

  nodes.push_back(SearchNode{
    start, -1, TfHopType::WALK, 0.0, heuristic(start), 0, 0.0f, 0.0f, false});
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

    neighbours(world, params, fly, cur.pos, goal, cur.airborne, edges);
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
        n.airborne = e.airborne;
        open.push(HeapItem{n.f, ni});
      } else {
        const int ni = static_cast<int>(nodes.size());
        const double nf = ng + heuristic(e.to);
        nodes.push_back(SearchNode{
          e.to, curIdx, e.type, ng, nf, nextMana, e.yaw, e.pitch, e.airborne});
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
    // Code 3 = airborne instant transmission (sky chain) so the executor can
    // use fast mid-air cast timing and not treat "not on ground" as overshoot.
    int code = static_cast<int>(n.type);
    if (n.type == TfHopType::AOTV && n.airborne) code = 3;
    result.hopTypes.push_back(code);
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
