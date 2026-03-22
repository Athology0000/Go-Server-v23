#pragma once
#include "../../include/Types.h"
#include <cstdint>

class WorldAccessor {
public:
    void setBuffer(const uint8_t* buf, int bx, int by, int bz);

    uint8_t getBlock(int x, int y, int z) const;
    bool isSolid(int x, int y, int z) const;
    bool isPassable(int x, int y, int z) const;
    bool isWalkable(int x, int y, int z) const;
    bool isLadder(int x, int y, int z) const;
    bool isWater(int x, int y, int z) const;
    bool isLava(int x, int y, int z) const;
    bool inBuffer(int x, int y, int z) const;

    const uint8_t* bufferData() const { return buffer_; }
    int originX() const { return bx_; }
    int originY() const { return by_; }
    int originZ() const { return bz_; }

private:
    const uint8_t* buffer_ = nullptr;
    int bx_ = 0, by_ = 0, bz_ = 0;

    uint8_t bufferAt(int x, int y, int z) const;
    uint8_t callbackBlock(int x, int y, int z) const;
};
