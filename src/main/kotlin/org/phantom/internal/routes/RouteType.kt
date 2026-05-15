package org.phantom.internal.routes

enum class RouteType(val label: String, val color: Long) {
    ORE_MINER("Ore Miner",   0xFF4DE2C5),
    COMMISSION("Commission", 0xFFFFA24F),
    PATROL("Patrol",         0xFFFF6B8A),
    GEMSTONE("Gemstone",     0xFF9E7CFF),
    TUNNEL("Tunnel",         0xFF60A5FA),
}

/** The sub-route inside a SavedRoute that a point belongs to. */
enum class SubRouteKey(val label: String, val icon: String) {
    TRAVEL("Travel Route", "\uD83D\uDEB6"),   // ðŸš¶
    LOOP("Loop Route",     "\uD83D\uDD04"),    // ðŸ”„
    AREA("Patrol Area",    "\u2694"),          // âš”
    POINTS("Route Points", "\uD83D\uDCC4"),   // ðŸ“„
}

enum class RoutePointType(val id: String, val label: String, val icon: String) {
    WALK("walk", "Walk", "\uD83D\uDEB6"),       // ðŸš¶
    WARP("warp", "Warp", "\u26A1"),             // âš¡
    MINE("mine", "Anchor", "\u26CF"),           // â›  (mine anchor point)
    VEIN("vein", "Vein", "\uD83D\uDCA0"),       // ðŸ’   (vein mining point)
    LANTERN("lantern", "Lantern", "\uD83D\uDD6F"), // ðŸ•¯  (auto-lantern placement)
    KILL("kill", "Kill", "\u2694");             // âš”

    companion object {
        fun fromId(id: String?): RoutePointType =
            entries.firstOrNull { it.id == id?.lowercase() } ?: WALK
    }
}

/** Which point types are valid for a given (RouteType, SubRouteKey) pair. */
fun allowedPointTypes(type: RouteType, sub: SubRouteKey): List<RoutePointType> = when (type) {
    RouteType.ORE_MINER -> when (sub) {
        SubRouteKey.TRAVEL -> listOf(RoutePointType.WALK, RoutePointType.WARP)
        SubRouteKey.LOOP   -> listOf(RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN)
        else -> emptyList()
    }
    RouteType.COMMISSION -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE)
        else -> emptyList()
    }
    RouteType.PATROL -> when (sub) {
        SubRouteKey.TRAVEL -> listOf(RoutePointType.WALK, RoutePointType.WARP)
        SubRouteKey.AREA   -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.KILL)
        else -> emptyList()
    }
    RouteType.GEMSTONE -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN)
        else -> emptyList()
    }
    RouteType.TUNNEL -> when (sub) {
        SubRouteKey.POINTS -> listOf(RoutePointType.WALK, RoutePointType.WARP, RoutePointType.MINE, RoutePointType.VEIN, RoutePointType.LANTERN)
        else -> emptyList()
    }
}

/** Which sub-routes a route type uses, in display order. */
fun subRoutesFor(type: RouteType): List<SubRouteKey> = when (type) {
    RouteType.ORE_MINER  -> listOf(SubRouteKey.TRAVEL, SubRouteKey.LOOP)
    RouteType.PATROL     -> listOf(SubRouteKey.TRAVEL, SubRouteKey.AREA)
    RouteType.COMMISSION,
    RouteType.GEMSTONE,
    RouteType.TUNNEL     -> listOf(SubRouteKey.POINTS)
}
