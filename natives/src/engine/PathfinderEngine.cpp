#include "PathfinderEngine.h"

PathfinderEngine::PathfinderEngine() {}

PathCommand PathfinderEngine::update(const uint8_t* buf,
                                      int bx, int by, int bz,
                                      double px, double py, double pz,
                                      float yaw, float pitch, bool onGround) {
    world_.setBuffer(buf, bx, by, bz);
    return executor_.tick(world_, px, py, pz, yaw, pitch, onGround);
}

void PathfinderEngine::setRoute(const double* data, int count, bool loop,
                                  int profile, double arrivalRadius) {
    std::vector<Vec3d> wps;
    wps.reserve(count);
    for (int i = 0; i < count * 3; i += 3)
        wps.push_back({data[i], data[i+1], data[i+2]});
    executor_.setRoute(wps, loop, (MovementProfile)profile, arrivalRadius);
}

void PathfinderEngine::setTarget(double x, double y, double z, double arrivalRadius) {
    executor_.setTarget({x, y, z}, arrivalRadius);
}

void PathfinderEngine::stop() { executor_.stop(); }
PathStatus PathfinderEngine::getStatus() const { return executor_.getStatus(); }

void PathfinderEngine::getPathNodes(std::vector<Vec3i>& out) const {
    out = executor_.getActivePath();
}
