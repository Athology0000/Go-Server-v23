#pragma once
#include "../../include/Types.h"
#include <deque>

class StuckDetector {
public:
    StuckDetector(int stuckTickLimit = 40, double movementEps = 0.05);

    void update(double px, double py, double pz);
    bool isStuck() const;
    void reset();

private:
    int stuckTickLimit_;
    double movementEps_;
    std::deque<Vec3d> history_;
    bool stuck_ = false;
};
