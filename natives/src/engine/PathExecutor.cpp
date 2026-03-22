#include "PathExecutor.h"
#include <cmath>

PathExecutor::PathExecutor() {}

void PathExecutor::setRoute(const std::vector<Vec3d>& wps, bool loop,
                             MovementProfile p, double arrivalRadius) {
    waypoints_ = wps; loop_ = loop; profile_ = p; arrivalRadius_ = arrivalRadius;
    waypointIdx_ = 0; activePath_.clear(); pathNodeIdx_ = 0;
    planner_.cancel(); stuck_.reset(); rotation_.reset();
    status_ = PathStatus::PLANNING;
}

void PathExecutor::setTarget(Vec3d t, double arrivalRadius) {
    setRoute({t}, false, profile_, arrivalRadius);
}

void PathExecutor::stop() {
    planner_.cancel(); status_ = PathStatus::IDLE;
    activePath_.clear(); waypoints_.clear();
}

void PathExecutor::startPlan(const WorldAccessor& world, double px, double py, double pz) {
    if (waypointIdx_ >= (int)waypoints_.size()) { status_ = PathStatus::ARRIVED; return; }
    Vec3d goal = waypoints_[waypointIdx_];
    Vec3i startI{(int)px, (int)py, (int)pz};
    Vec3i goalI {(int)goal.x, (int)goal.y, (int)goal.z};
    planner_.startAsync(startI, goalI, world, profile_);
    status_ = PathStatus::PLANNING;
}

float PathExecutor::distToWaypoint(double px, double pz) const {
    if (waypointIdx_ >= (int)waypoints_.size()) return 0.f;
    const Vec3d& wp = waypoints_[waypointIdx_];
    double dx = wp.x - px, dz = wp.z - pz;
    return (float)std::sqrt(dx*dx + dz*dz);
}

PathCommand PathExecutor::buildCommand(double px, double py, double pz,
                                        float yaw, float pitch) {
    float dist = distToWaypoint(px, pz);

    if (pathNodeIdx_ >= (int)activePath_.size()) {
        // Finished path nodes — check arrival
        if (dist <= (float)arrivalRadius_) {
            waypointIdx_++;
            if (loop_ && waypointIdx_ >= (int)waypoints_.size()) waypointIdx_ = 0;
            if (waypointIdx_ >= (int)waypoints_.size()) {
                status_ = PathStatus::ARRIVED;
                return {false,false,false,false,false,yaw,pitch,PathStatus::ARRIVED,ActionType::WALK,dist};
            }
            status_ = PathStatus::REPLANNING;
            return {false,false,false,false,false,yaw,pitch,PathStatus::REPLANNING,ActionType::WALK,dist};
        }
        status_ = PathStatus::REPLANNING;
        return {false,false,false,false,false,yaw,pitch,PathStatus::REPLANNING,ActionType::WALK,dist};
    }

    Vec3i target = activePath_[pathNodeIdx_];
    double dx = (target.x + 0.5) - px;
    double dz = (target.z + 0.5) - pz;
    double dy = target.y - py;
    double nodeDist = std::sqrt(dx*dx + dz*dz);

    if (nodeDist < NODE_DIST && std::abs(dy) < 1.5) {
        pathNodeIdx_++;
        rotation_.setPath(activePath_, pathNodeIdx_);
    }

    bool jump   = dy > 0.5;
    bool sprint = nodeDist > 1.5;

    float outYaw, outPitch;
    rotation_.tick(px, py, pz, yaw, pitch, outYaw, outPitch);

    return {true, false, jump, false, sprint,
            outYaw, outPitch, PathStatus::EXECUTING, ActionType::SPRINT, dist};
}

PathCommand PathExecutor::tick(const WorldAccessor& world,
                                double px, double py, double pz,
                                float yaw, float pitch, bool onGround) {
    stuck_.update(px, py, pz);

    switch (status_) {
    case PathStatus::IDLE:
        return idleCmd(yaw, pitch);

    case PathStatus::PLANNING:
        if (planner_.isComplete()) {
            auto res = planner_.takeResult();
            if (!res.found && !res.isPartial) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
            activePath_ = std::move(res.nodes);
            pathNodeIdx_ = 0;
            rotation_.setPath(activePath_, 0);
            stuck_.reset();
            status_ = PathStatus::EXECUTING;
        } else if (!planner_.isRunning()) {
            startPlan(world, px, py, pz);
        }
        return {false,false,false,false,false,yaw,pitch,PathStatus::PLANNING,ActionType::WALK,distToWaypoint(px,pz)};

    case PathStatus::REPLANNING:
        if (planner_.isComplete()) {
            auto res = planner_.takeResult();
            if (!res.found && !res.isPartial) { status_ = PathStatus::FAILED; return idleCmd(yaw, pitch); }
            activePath_ = std::move(res.nodes);
            pathNodeIdx_ = 0;
            rotation_.setPath(activePath_, 0);
            stuck_.reset();
            status_ = PathStatus::EXECUTING;
        } else if (!planner_.isRunning()) {
            startPlan(world, px, py, pz);
        }
        // Keep moving on old path while replanning
        if (!activePath_.empty())
            return buildCommand(px, py, pz, yaw, pitch);
        return {false,false,false,false,false,yaw,pitch,PathStatus::REPLANNING,ActionType::WALK,distToWaypoint(px,pz)};

    case PathStatus::EXECUTING:
        if (stuck_.isStuck()) {
            recoverTicks_ = 0;
            status_ = PathStatus::RECOVERING;
        }
        return buildCommand(px, py, pz, yaw, pitch);

    case PathStatus::RECOVERING:
        recoverTicks_++;
        if (recoverTicks_ > RECOVER_TIMEOUT) {
            startPlan(world, px, py, pz);
        } else if (!stuck_.isStuck()) {
            status_ = PathStatus::EXECUTING;
        }
        return {false, true, false, false, false, yaw, pitch,
                PathStatus::RECOVERING, ActionType::WALK, distToWaypoint(px,pz)};

    case PathStatus::ARRIVED:
    case PathStatus::FAILED:
        return {false,false,false,false,false,yaw,pitch,status_,ActionType::WALK,0.f};
    }
    return idleCmd(yaw, pitch);
}
