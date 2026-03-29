#include "WorldAccessor.h"

void WorldAccessor::setBuffer(const uint8_t* buf, int bx, int by, int bz) {
    buffer_ = buf; bx_ = bx; by_ = by; bz_ = bz;
}

bool WorldAccessor::inBuffer(int x, int y, int z) const {
    return buffer_ &&
        x >= bx_ && x < bx_ + BUF_W &&
        y >= by_ && y < by_ + BUF_H &&
        z >= bz_ && z < bz_ + BUF_D;
}

uint8_t WorldAccessor::bufferAt(int x, int y, int z) const {
    int idx = (x - bx_) + (z - bz_) * BUF_STRIDE_Z + (y - by_) * BUF_STRIDE_Y;
    return buffer_[idx];
}

uint8_t WorldAccessor::callbackBlock(int, int, int) const {
    // JNI callbacks from background threads require AttachCurrentThread - not implemented.
    // Buffer covers all Skyblock pathfinding; treat out-of-range as SOLID.
    return BT_SOLID;
}

uint8_t WorldAccessor::getBlock(int x, int y, int z) const {
    return inBuffer(x, y, z) ? bufferAt(x, y, z) : callbackBlock(x, y, z);
}

bool WorldAccessor::isSolid(int x, int y, int z) const    { auto b = getBlock(x,y,z); return b == BT_SOLID || b == BT_STEP; }
bool WorldAccessor::isPassable(int x, int y, int z) const { auto b = getBlock(x,y,z); return b == BT_AIR || b == BT_WATER || b == BT_LADDER; }
bool WorldAccessor::isLadder(int x, int y, int z) const   { return getBlock(x,y,z) == BT_LADDER; }
bool WorldAccessor::isWater(int x, int y, int z) const    { return getBlock(x,y,z) == BT_WATER; }
bool WorldAccessor::isLava(int x, int y, int z) const     { return getBlock(x,y,z) == BT_LAVA; }

bool WorldAccessor::isWalkable(int x, int y, int z) const {
    return isSolid(x, y, z) && isPassable(x, y+1, z) && isPassable(x, y+2, z);
}
