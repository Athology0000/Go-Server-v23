#include "StuckDetector.h"
#include <cmath>

StuckDetector::StuckDetector(int limit, double eps)
    : stuckTickLimit_(limit), movementEps_(eps) {}

void StuckDetector::update(double px, double py, double pz) {
    history_.push_back({px, py, pz});
    if ((int)history_.size() > stuckTickLimit_) history_.pop_front();

    if ((int)history_.size() < stuckTickLimit_) { stuck_ = false; return; }

    const auto& oldest = history_.front();
    double dx = px - oldest.x, dy = py - oldest.y, dz = pz - oldest.z;
    double moved = std::sqrt(dx*dx + dy*dy + dz*dz);
    stuck_ = (moved < movementEps_);
}

bool StuckDetector::isStuck() const { return stuck_; }
void StuckDetector::reset() { history_.clear(); stuck_ = false; }
