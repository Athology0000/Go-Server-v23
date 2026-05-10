package org.cobalt.internal.mining

import java.awt.Color
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

internal data class WarpCheck(
  val canWarp: Boolean,
  val reason: String,
  val aimPoint: Vec3,
  val hitBlock: BlockPos?,
  val raycastTarget: BlockPos? = null
)

internal enum class RouteAction {
  NONE,
  WALK,
  WARP,
  MINE
}

internal enum class RouteMineMode {
  NONE,
  LEGACY_VEIN,
  ANCHOR_LIST,
  SINGLE_ANCHOR_MACRO
}

internal const val MAX_WALK_RETRIES = 3
internal const val WALK_REPATH_GRACE_TICKS = 8L
internal const val RDBT_ROUTE_ARRIVAL_RADIUS = 3.0
internal const val RDBT_ROUTE_ARRIVAL_DISTANCE_SQ = RDBT_ROUTE_ARRIVAL_RADIUS * RDBT_ROUTE_ARRIVAL_RADIUS
internal const val ARRIVAL_DISTANCE_SQ = 1.8 * 1.8
internal const val WALK_EDGE_SLOW_DISTANCE_SQ = 2.15 * 2.15
internal const val WALK_EDGE_ARRIVAL_DISTANCE_SQ = 1.9 * 1.9
internal const val WALK_EDGE_ARRIVAL_VERTICAL = 1.35
internal const val WALK_CAMERA_FORWARD_SEARCH_WINDOW = 16
internal const val WALK_EDGE_NODE_LOOKAHEAD = 2
internal const val ROUTE_SEGMENT_ARRIVAL_RADIUS = 0.95
internal const val RISKY_ROUTE_SEGMENT_ARRIVAL_RADIUS = 0.7
internal const val ROUTE_SINGLE_WALK_ARRIVAL_RADIUS = 1.05
internal const val RISKY_ROUTE_SINGLE_WALK_ARRIVAL_RADIUS = 0.8
internal const val WALK_EDGE_ARRIVAL_EXTRA_RADIUS = 0.25
internal const val MIN_WALK_EDGE_ARRIVAL_RADIUS = 0.95
internal const val SHARP_TURN_COS_THRESHOLD = 0.78
internal const val SHARP_TURN_MIN_VECTOR = 0.2
internal const val APPROX_SCAN_RADIUS = 6
internal const val APPROX_SCAN_VERTICAL = 4
internal const val MINE_APPROACH_AT_DIST_SQ = 2.25
internal const val MINE_RANGE = 4.5
internal const val MINE_ANCHOR_SCAN_RADIUS = 4
internal const val MINE_REQUIRE_LOS = true
internal const val MINE_MAX_BLOCKS = 768
internal const val MINE_VEIN_SCAN_RADIUS = 18
internal const val MINE_WARP_MAX_CANDIDATES = 220
internal const val MINE_DRILL_WARN_INTERVAL_TICKS = 60L
internal const val WARP_AIM_TOLERANCE = 6.0
internal const val WARP_LOOK_YAW_SPEED_DPS = 360.0
internal const val WARP_LOOK_PITCH_SPEED_DPS = 300.0
internal const val WARP_ALIGN_MS = 170.0
internal const val WARP_SNEAK_MS = 85.0
internal const val WARP_POST_MS = 70.0
internal const val WARP_CHAIN_POST_MS = 28.0
internal const val WARP_COOLDOWN_TICKS = 1L
internal const val WARP_STAGE1_TIMEOUT_MS = 240.0
internal const val WARP_RETRY_COOLDOWN_TICKS = 4L
internal const val WARP_REPEAT_BLOCK_SUPPRESS_TICKS = 10L
internal const val WARP_RESOLVE_RADIUS = 2
internal const val WARP_RESOLVE_VERTICAL = 2

internal data class MineVein(
  val blockId: String,
  val blocks: MutableSet<BlockPos>
)

internal data class MineOrderEntry(
  val pos: BlockPos,
  val progress: Double,
  val lateralDistSq: Double,
  val startDistSq: Double
)

internal data class MineSegment(
  val startIndex: Int,
  val endIndex: Int,
  val anchors: List<BlockPos>,
  val waypoints: List<BlockPos>,
  val anchorBlockIds: Map<BlockPos, String?>,
  val legacyVein: Boolean
)

internal data class RouteRenderNode(
  val pos: BlockPos,
  val color: Color,
  val label: String?
)
