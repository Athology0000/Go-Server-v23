#pragma once
#include "../../include/Types.h"
#include "WorldAccessor.h"
#include "PathExecutor.h"

class PathfinderEngine {
public:
    PathfinderEngine();

    PathCommand update(const uint8_t* worldBuf,
                       int bx, int by, int bz,
                       double px, double py, double pz,
                       float yaw, float pitch, bool onGround);

    void setRoute(const double* waypointData, int count, bool loop, int profile,
                  double arrivalRadius = 1.8);
    void setTarget(double x, double y, double z, double arrivalRadius = 1.8);
    void stop();
    PathStatus getStatus() const;
    void getPathNodes(std::vector<Vec3i>& out) const;

private:
    WorldAccessor world_;
    PathExecutor executor_;
};
