#include "RotationController.h"
#include <cmath>
#include <algorithm>

static constexpr float PI = 3.14159265f;

RotationController::RotationController() {}

void RotationController::setPath(const std::vector<Vec3i>& nodes, int idx) {
    path_ = nodes; currentIdx_ = idx;
}
void RotationController::setCombatMode(bool c) { combatMode_ = c; }
void RotationController::reset() {
    path_.clear();
    currentIdx_ = 0;
    smoothYaw_ = 0;
    smoothPitch_ = 0;
    initialized_ = false;
    noiseTick_ = 0;
    noiseInterval_ = 12;
}

float RotationController::pseudoRand() {
    noiseSeed_ = noiseSeed_ * 1664525 + 1013904223;
    return ((noiseSeed_ & 0x7fff) / (float)0x7fff) * 2.0f - 1.0f;
}

float RotationController::noise(float scale) {
    return pseudoRand() * scale;
}

float RotationController::angleDiff(float a, float b) const {
    float d = a - b;
    while (d >  180) d -= 360;
    while (d < -180) d += 360;
    return d;
}

float RotationController::lookAheadYaw(double px, double py, double pz) const {
    if (path_.empty()) return smoothYaw_;
    // Average the direction to the next 4 nodes rather than targeting a single far node.
    //
    // Single-node lookahead at +2 → target yaw flips every step on zigzag diagonal paths
    // (E→N→E→N), causing visible left/right oscillation.
    //
    // Single-node lookahead at +6 → fixes oscillation BUT looking too far ahead on a
    // sharp turn means the player faces the wrong direction, runs past the current node,
    // can't reach it (NODE_DIST = 0.6), and circles indefinitely; also causes 180° snaps.
    //
    // Averaging 4 nodes: zigzag E,N,E,N → centroid is NE → stable heading ✓
    //                    sharp turn after 3 nodes → centroid shifts smoothly toward the
    //                    turn as nodes advance → responsive ✓
    const int LOOKAHEAD = 4;
    double sumX = 0.0, sumZ = 0.0;
    int count = 0;
    for (int i = 1; i <= LOOKAHEAD; i++) {
        int idx = std::min(currentIdx_ + i, (int)path_.size() - 1);
        sumX += path_[idx].x + 0.5;
        sumZ += path_[idx].z + 0.5;
        ++count;
    }
    if (count == 0) return smoothYaw_;
    double dx = sumX / count - px;
    double dz = sumZ / count - pz;
    if (dx * dx + dz * dz < 0.01) return smoothYaw_;
    return (float)(std::atan2(-dx, dz) * 180.0 / PI);
}

float RotationController::lookAheadPitch(double px, double py, double pz) const {
    int ahead = std::min(currentIdx_ + 2, (int)path_.size() - 1);
    if (ahead < 0 || path_.empty()) return 0;
    const auto& t = path_[ahead];
    double dx = t.x + 0.5 - px, dz = t.z + 0.5 - pz;
    double horiz = std::sqrt(dx*dx + dz*dz);
    double dy = (t.y + 0.5) - (py + 1.62);
    float pitch = (float)(-std::atan2(dy, horiz) * 180.0 / PI);
    return std::max(-45.0f, std::min(45.0f, pitch));
}

void RotationController::tick(double px, double py, double pz,
                               float curYaw, float curPitch,
                               float& outYaw, float& outPitch) {
    if (path_.empty()) { outYaw = curYaw; outPitch = curPitch; return; }
    if (!initialized_) {
        smoothYaw_ = curYaw;
        smoothPitch_ = curPitch;
        initialized_ = true;
    }

    float targetYaw   = lookAheadYaw(px, py, pz);
    float targetPitch = lookAheadPitch(px, py, pz);

    // Bezier-like easing - raw output, no GCD (RotationExecutor applies GCD on Kotlin side)
    float speed = combatMode_ ? 0.35f : 0.18f;
    float yawDiff   = angleDiff(targetYaw,   smoothYaw_);
    float pitchDiff = targetPitch - smoothPitch_;

    smoothYaw_   += yawDiff   * speed;
    smoothPitch_ += pitchDiff * speed;

    // Micro-noise every noiseInterval_ ticks
    if (++noiseTick_ >= noiseInterval_) {
        noiseTick_ = 0;
        noiseInterval_ = 8 + (int)(pseudoRand() * 3.5f + 3.5f);
        smoothYaw_   += noise(0.15f);
        smoothPitch_ += noise(0.08f);
    }

    outYaw   = smoothYaw_;
    outPitch = std::max(-45.0f, std::min(45.0f, smoothPitch_));
}
