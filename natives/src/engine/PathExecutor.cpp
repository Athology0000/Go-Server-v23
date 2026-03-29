#include "PathExecutor.h"
#include <cmath>

namespace {
int blockCoord(double value) {
    return static_cast<int>(std::floor(value));
}
}

PathExecutor::PathExecutor() {}

void PathExecutor::setRoute(const std::vector<Vec3d>& wps, bool loop,
                             MovementProfile p, double arrivalRadius) {
    waypoints_ = wps;
    loop_ = loop;
    profile_ = p;
    arrivalRadius_ = arrivalRadius;
    waypointIdx_ = 0;
    activePath_.clear();
    activePathPositions_.clear();
    pathNodeIdx_ = 0;
    recoverTicks_ = 0;
    planner_.cancel();
    stuck_.reset();
    rotation_.reset();
    status_ = PathStatus::PLANNING;
}

void PathExecutor::setTarget(Vec3d t, double arrivalRadius) {
    setRoute({t}, false, profile_, arrivalRadius);
}

void PathExecutor::stop() {
    planner_.cancel();
    status_ = PathStatus::IDLE;
    activePath_.clear();
    activePathPositions_.clear();
    waypoints_.clear();
}

void PathExecutor::startPlan(const WorldAccessor& world, double px, double py, double pz) {
    if (waypointIdx_ >= (int)waypoints_.size()) {
        status_ = PathStatus::ARRIVED;
        return;
    }

    Vec3d goal = waypoints_[waypointIdx_];
    Vec3i startI{blockCoord(px), blockCoord(py), blockCoord(pz)};
    Vec3i goalI{blockCoord(goal.x), blockCoord(goal.y), blockCoord(goal.z)};
    planner_.startAsync(startI, goalI, world, profile_);
    status_ = PathStatus::PLANNING;
}

void PathExecutor::setActivePath(std::vector<PathNode> nodes) {
    activePath_ = std::move(nodes);
    activePathPositions_.clear();
    activePathPositions_.reserve(activePath_.size());
    for (const auto& node : activePath_) {
        activePathPositions_.push_back(node.pos);
    }
}

float PathExecutor::distToWaypoint(double px, double py, double pz) const {
    if (waypointIdx_ >= (int)waypoints_.size()) return 0.f;
    const Vec3d& wp = waypoints_[waypointIdx_];
    double dx = wp.x - px;
    double dy = wp.y - py;
    double dz = wp.z - pz;
    return (float)std::sqrt(dx * dx + dy * dy + dz * dz);
}

PathCommand PathExecutor::buildCommand(double px, double py, double pz,
                                        float yaw, float pitch) {
    float dist = distToWaypoint(px, py, pz);

    while (pathNodeIdx_ < (int)activePath_.size()) {
        const auto& current = activePath_[pathNodeIdx_];
        double dx = (current.pos.x + 0.5) - px;
        double dy = current.pos.y - py;
        double dz = (current.pos.z + 0.5) - pz;
        double nodeDist = std::sqrt(dx * dx + dy * dy + dz * dz);

        if (nodeDist < NODE_DIST) {
            pathNodeIdx_++;
            rotation_.setPath(activePathPositions_, pathNodeIdx_);
            continue;
        }

        bool forward = false;
        bool back = false;
        bool jump = false;
        bool sneak = false;
        bool sprint = false;

        switch (current.action) {
        case ActionType::WALK:
            forward = true;
            break;
        case ActionType::SPRINT:
            forward = true;
            sprint = true;
            break;
        case ActionType::JUMP:
            forward = true;
            jump = true;
            break;
        case ActionType::SPRINT_JUMP:
            forward = true;
            jump = true;
            sprint = true;
            break;
        case ActionType::FALL:
            forward = true;
            break;
        case ActionType::LADDER:
            forward = true;
            break;
        case ActionType::WATER_SWIM:
            forward = std::sqrt(dx * dx + dz * dz) > 0.15;
            jump = dy > 0.35;
            sneak = dy < -0.35;
            break;
        case ActionType::AOTV:
        case ActionType::ETHERWARP:
            // Teleport actions are not universally executed by all Kotlin callers.
            // Fall back to sprinting toward the node instead of hard-stalling.
            forward = true;
            sprint = std::sqrt(dx * dx + dz * dz) > 1.5;
            break;
        }

        float outYaw, outPitch;
        rotation_.tick(px, py, pz, yaw, pitch, outYaw, outPitch);

        return {forward, back, jump, sneak, sprint,
                outYaw, outPitch, PathStatus::EXECUTING, current.action, dist};
    }

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
            if (!res.found && !res.isPartial) {
                status_ = PathStatus::FAILED;
                return idleCmd(yaw, pitch);
            }
            setActivePath(std::move(res.nodes));
            pathNodeIdx_ = 0;
            rotation_.setPath(activePathPositions_, 0);
            stuck_.reset();
            status_ = PathStatus::EXECUTING;
        } else if (!planner_.isRunning()) {
            startPlan(world, px, py, pz);
        }
        return {false,false,false,false,false,yaw,pitch,PathStatus::PLANNING,ActionType::WALK,distToWaypoint(px,py,pz)};

    case PathStatus::REPLANNING:
        if (planner_.isComplete()) {
            auto res = planner_.takeResult();
            if (!res.found && !res.isPartial) {
                status_ = PathStatus::FAILED;
                return idleCmd(yaw, pitch);
            }
            setActivePath(std::move(res.nodes));
            pathNodeIdx_ = 0;
            rotation_.setPath(activePathPositions_, 0);
            stuck_.reset();
            status_ = PathStatus::EXECUTING;
        } else if (!planner_.isRunning()) {
            startPlan(world, px, py, pz);
        }
        if (!activePath_.empty()) {
            return buildCommand(px, py, pz, yaw, pitch);
        }
        return {false,false,false,false,false,yaw,pitch,PathStatus::REPLANNING,ActionType::WALK,distToWaypoint(px,py,pz)};

    case PathStatus::EXECUTING:
        if (stuck_.isStuck()) {
            recoverTicks_ = 0;
            status_ = PathStatus::RECOVERING;
            return {false, true, false, false, false, yaw, pitch,
                    PathStatus::RECOVERING, ActionType::WALK, distToWaypoint(px,py,pz)};
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
                PathStatus::RECOVERING, ActionType::WALK, distToWaypoint(px,py,pz)};

    case PathStatus::ARRIVED:
    case PathStatus::FAILED:
        return {false,false,false,false,false,yaw,pitch,status_,ActionType::WALK,0.f};
    }

    return idleCmd(yaw, pitch);
}
