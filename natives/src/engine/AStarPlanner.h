#pragma once
#include "../../include/Types.h"
#include "MovementExpander.h"
#include "WorldAccessor.h"
#include <vector>
#include <thread>
#include <mutex>
#include <atomic>

struct AStarResult {
    bool found     = false;
    bool isPartial = false;   // true = path leads to best frontier node, not the real goal
    std::vector<Vec3i> nodes;
};

class AStarPlanner {
public:
    ~AStarPlanner();

    // Snapshots the world buffer into the thread closure — no shared reference to WorldAccessor.
    void startAsync(Vec3i start, Vec3i goal,
                    const WorldAccessor& world,
                    MovementProfile profile);

    bool isComplete() const { return complete_.load(); }
    bool isRunning()  const { return running_.load(); }
    AStarResult takeResult();
    void cancel();

private:
    std::thread thread_;
    std::mutex  mutex_;
    std::atomic<bool> complete_{false};
    std::atomic<bool> running_{false};
    std::atomic<bool> cancelled_{false};
    AStarResult result_;

    static float heuristic(const Vec3i& a, const Vec3i& b);
    static MovementCosts costsForProfile(MovementProfile p);
};
