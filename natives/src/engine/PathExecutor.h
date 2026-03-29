#pragma once
#include "../../include/Types.h"
#include "AStarPlanner.h"
#include "StuckDetector.h"
#include "RotationController.h"
#include "WorldAccessor.h"
#include <vector>

class PathExecutor {
public:
    PathExecutor();

    void setRoute(const std::vector<Vec3d>& waypoints, bool loop, MovementProfile profile,
                  double arrivalRadius = 1.8);
    void setTarget(Vec3d target, double arrivalRadius = 1.8);
    void stop();

    PathCommand tick(const WorldAccessor& world,
                     double px, double py, double pz,
                     float yaw, float pitch, bool onGround);

    PathStatus getStatus() const { return status_; }
    const std::vector<Vec3i>& getActivePath() const { return activePathPositions_; }

private:
    PathStatus status_ = PathStatus::IDLE;
    MovementProfile profile_ = MovementProfile::DEFAULT;
    double arrivalRadius_ = 1.8;

    std::vector<Vec3d> waypoints_;
    int waypointIdx_ = 0;
    bool loop_ = false;

    std::vector<PathNode> activePath_;
    std::vector<Vec3i> activePathPositions_;
    int pathNodeIdx_ = 0;

    AStarPlanner planner_;
    StuckDetector stuck_;
    RotationController rotation_;

    int recoverTicks_ = 0;
    static constexpr int RECOVER_TIMEOUT = 60;
    static constexpr double NODE_DIST    = 0.6;

    void startPlan(const WorldAccessor& world, double px, double py, double pz);
    PathCommand buildCommand(double px, double py, double pz, float yaw, float pitch);
    PathCommand idleCmd(float yaw, float pitch) {
        return {false,false,false,false,false,yaw,pitch,PathStatus::IDLE,ActionType::WALK,0.f};
    }
    void setActivePath(std::vector<PathNode> nodes);
    float distToWaypoint(double px, double py, double pz) const;
};
