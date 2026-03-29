#pragma once
#include "../../include/Types.h"
#include <vector>

class RotationController {
public:
    RotationController();

    void setPath(const std::vector<Vec3i>& nodes, int currentIdx);
    void setCombatMode(bool combat);

    void tick(double px, double py, double pz,
              float currentYaw, float currentPitch,
              float& outYaw, float& outPitch);

    void reset();

private:
    std::vector<Vec3i> path_;
    int currentIdx_ = 0;
    bool combatMode_ = false;

    float smoothYaw_   = 0;
    float smoothPitch_ = 0;
    bool initialized_  = false;
    int   noiseTick_   = 0;
    int   noiseInterval_ = 12;
    int   noiseSeed_   = 1337;

    float angleDiff(float a, float b) const;
    float lookAheadYaw(double px, double py, double pz) const;
    float lookAheadPitch(double px, double py, double pz) const;
    float noise(float scale);
    float pseudoRand();
};
