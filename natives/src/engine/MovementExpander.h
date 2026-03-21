#pragma once
#include "../../include/Types.h"
#include "WorldAccessor.h"
#include <vector>

struct PathNode {
    Vec3i pos;
    ActionType action;
    float cost;
};

struct MovementCosts {
    float walk       = 1.0f;
    float sprint     = 0.8f;
    float jump       = 1.2f;
    float sprintJump = 0.5f;
    float fall       = 0.9f;
    float ladder     = 1.1f;
    float swim       = 2.0f;
    float aotv       = 5.0f;
    float etherwarp  = 4.0f;
    float lavaAdj    = 20.0f;
    float waterSrc   = 5.0f;
};

class MovementExpander {
public:
    explicit MovementExpander(const WorldAccessor& world,
                               const MovementCosts& costs = {});
    std::vector<PathNode> expand(const Vec3i& from) const;

private:
    const WorldAccessor& world_;
    MovementCosts costs_;

    void addWalkSprint(const Vec3i& from, std::vector<PathNode>& out) const;
    void addJumps(const Vec3i& from, std::vector<PathNode>& out) const;
    void addFall(const Vec3i& from, std::vector<PathNode>& out) const;
    void addLadder(const Vec3i& from, std::vector<PathNode>& out) const;
    void addSwim(const Vec3i& from, std::vector<PathNode>& out) const;
    void addAOTV(const Vec3i& from, std::vector<PathNode>& out) const;
    void addEtherwarp(const Vec3i& from, std::vector<PathNode>& out) const;

    float adjacentCost(const Vec3i& pos) const;
    float clearanceCost(const Vec3i& pos) const;
    bool clearanceOk(int x, int y, int z) const;
};
