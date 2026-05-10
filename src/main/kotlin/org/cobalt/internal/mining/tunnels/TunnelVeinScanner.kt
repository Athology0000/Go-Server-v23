package org.cobalt.internal.mining.tunnels

import java.util.ArrayDeque
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.AirBlock
import net.minecraft.world.level.block.Blocks
import org.cobalt.api.pathfinder.scoring.WaypointScorer
import org.cobalt.internal.mining.routes.OrderedMiningRouteCursor
import org.cobalt.internal.routes.RoutePointType

object TunnelVeinScanner {

  data class TunnelTarget(
    val ore: TunnelOreType,
    val veinIndex: Int,
    val standPos: BlockPos,
    val edgeBlock: BlockPos,
    val veinBlocks: List<BlockPos>,
    val score: Double,
    val source: TargetSource,
    val routeName: String? = null,
    val routeIndex: Int = -1,
    val routePointType: RoutePointType = RoutePointType.MINE,
    val routeBlockId: String? = null
  )

  enum class TargetSource {
    ROUTE_STORE_ORDERED,
    ROUTE_STORE,
    RECORDED_DATA,
    DYNAMIC_SCAN
  }

  private val edgeOffsets = listOf(
    BlockPos(1, 0, 0),
    BlockPos(-1, 0, 0),
    BlockPos(0, 1, 0),
    BlockPos(0, -1, 0),
    BlockPos(0, 0, 1),
    BlockPos(0, 0, -1)
  )

  private val horizontalOffsets = listOf(
    BlockPos(1, 0, 0),
    BlockPos(-1, 0, 0),
    BlockPos(0, 0, 1),
    BlockPos(0, 0, -1),
    BlockPos(1, 0, 1),
    BlockPos(1, 0, -1),
    BlockPos(-1, 0, 1),
    BlockPos(-1, 0, -1)
  )

  private val recentBadStandPositions = LinkedHashMap<BlockPos, Int>()

  private const val RECENT_BAD_TTL = 5
  private const val MAX_RECENT_BAD = 64

  fun markBadStand(pos: BlockPos?) {
    if (pos == null) return

    recentBadStandPositions[pos] = RECENT_BAD_TTL

    while (recentBadStandPositions.size > MAX_RECENT_BAD) {
      val first = recentBadStandPositions.keys.firstOrNull() ?: break
      recentBadStandPositions.remove(first)
    }
  }

  fun markRouteTargetCompleted(target: TunnelTarget?) {
    if (target == null) return
    if (target.routeName.isNullOrBlank()) return
    if (target.routeIndex < 0) return

    OrderedMiningRouteCursor.markCompleted(
      kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
      routeName = target.routeName,
      routeSize = routeSizeFor(target.routeName),
      index = target.routeIndex
    )
  }

  fun markRouteTargetFailed(target: TunnelTarget?) {
    if (target == null) return

    markBadStand(target.standPos)

    if (target.routeName.isNullOrBlank()) return
    if (target.routeIndex < 0) return

    OrderedMiningRouteCursor.markFailed(
      kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
      routeName = target.routeName,
      routeSize = routeSizeFor(target.routeName),
      index = target.routeIndex
    )
  }

  fun resetBadStands() {
    recentBadStandPositions.clear()
  }

  fun decayBadStands() {
    val updated = recentBadStandPositions
      .mapValues { (_, ttl) -> ttl - 1 }
      .filterValues { ttl -> ttl > 0 }

    recentBadStandPositions.clear()
    recentBadStandPositions.putAll(updated)
  }

  fun scan(
    selectedOres: Set<TunnelOreType>,
    maxTargets: Int = 80,
    dynamicScanRadius: Int = 22,
    includeRecordedData: Boolean = true,
    includeDynamicScan: Boolean = true,
    includeRouteStore: Boolean = true,
    preferOrderedRoutes: Boolean = true,
    explicitRouteName: String? = null
  ): List<TunnelTarget> {
    decayBadStands()

    val targets = mutableListOf<TunnelTarget>()

    if (includeRouteStore && preferOrderedRoutes) {
      val ordered = scanOrderedRouteStore(
        selectedOres = selectedOres,
        explicitRouteName = explicitRouteName
      )

      if (ordered.isNotEmpty()) {
        return ordered
          .filterNot { recentBadStandPositions.containsKey(it.standPos) }
          .take(maxTargets)
      }
    }

    if (includeRouteStore) {
      targets += scanRouteStore(
        selectedOres = selectedOres,
        maxTargets = maxTargets,
        explicitRouteName = explicitRouteName
      )
    }

    if (includeRecordedData && targets.size < maxTargets) {
      targets += scanRecordedData(
        selectedOres = selectedOres,
        maxTargets = maxTargets - targets.size
      )
    }

    if (includeDynamicScan && targets.size < maxTargets) {
      targets += scanDynamicWorld(
        selectedOres = selectedOres,
        maxTargets = maxTargets - targets.size,
        radius = dynamicScanRadius
      )
    }

    return targets
      .distinctBy {
        "${it.ore.name}:${it.standPos.x}:${it.standPos.y}:${it.standPos.z}:${it.edgeBlock.x}:${it.edgeBlock.y}:${it.edgeBlock.z}"
      }
      .filterNot { recentBadStandPositions.containsKey(it.standPos) }
      .sortedBy { it.score }
      .take(maxTargets)
  }

  private fun scanOrderedRouteStore(
    selectedOres: Set<TunnelOreType>,
    explicitRouteName: String?
  ): List<TunnelTarget> {
    val player = Minecraft.getInstance().player ?: return emptyList()

    val loaded = TunnelRouteStoreBridge.loadActiveTunnelRoute(
      fallbackOres = selectedOres,
      explicitRouteName = explicitRouteName
    ) ?: return emptyList()

    if (!loaded.isValid) return emptyList()

    val routePoints = loaded.points.filter { it.enabled }

    if (routePoints.isEmpty()) return emptyList()

    val playerBlock = player.blockPosition()

    val cursorState = OrderedMiningRouteCursor.state(
      kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
      routeName = loaded.routeName
    )

    if (cursorState == null) {
      OrderedMiningRouteCursor.chooseNearestIndex(
        kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
        routeName = loaded.routeName,
        points = routePoints.map { it.standPos },
        playerPos = playerBlock
      )
    }

    val index = OrderedMiningRouteCursor.nextIndex(
      kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
      routeName = loaded.routeName,
      routeSize = routePoints.size,
      allowFailed = false
    )

    if (index !in routePoints.indices) return emptyList()

    val point = routePoints[index]

    if (recentBadStandPositions.containsKey(point.standPos)) {
      OrderedMiningRouteCursor.markFailed(
        kind = OrderedMiningRouteCursor.RouteKind.TUNNEL,
        routeName = loaded.routeName,
        routeSize = routePoints.size,
        index = index
      )

      return emptyList()
    }

    val selectedOre = point.oreTypes.firstOrNull { ore ->
      selectedOres.isEmpty() || selectedOres.contains(ore)
    } ?: return emptyList()

    val veinBlocks = collectNearbyOreBlocks(
      center = point.veinPos,
      ores = point.oreTypes,
      radius = 5
    ).ifEmpty {
      listOf(point.veinPos)
    }

    val score = WaypointScorer.scoreWaypoint(
      playerPos = player.position(),
      waypoint = point.standPos
    ).totalCost - 50.0

    return listOf(
      TunnelTarget(
        ore = selectedOre,
        veinIndex = index,
        standPos = point.standPos,
        edgeBlock = point.veinPos,
        veinBlocks = veinBlocks,
        score = score,
        source = TargetSource.ROUTE_STORE_ORDERED,
        routeName = loaded.routeName,
        routeIndex = index,
        routePointType = point.pointType,
        routeBlockId = point.blockId
      )
    )
  }

  private fun scanRouteStore(
    selectedOres: Set<TunnelOreType>,
    maxTargets: Int,
    explicitRouteName: String?
  ): List<TunnelTarget> {
    val player = Minecraft.getInstance().player ?: return emptyList()

    val loaded = TunnelRouteStoreBridge.loadActiveTunnelRoute(
      fallbackOres = selectedOres,
      explicitRouteName = explicitRouteName
    ) ?: return emptyList()

    val targets = mutableListOf<TunnelTarget>()

    for ((index, point) in loaded.points.withIndex()) {
      if (!point.enabled) continue
      if (recentBadStandPositions.containsKey(point.standPos)) continue

      val selectedOre = point.oreTypes.firstOrNull { ore ->
        selectedOres.isEmpty() || selectedOres.contains(ore)
      } ?: continue

      val veinBlocks = collectNearbyOreBlocks(
        center = point.veinPos,
        ores = point.oreTypes,
        radius = 5
      ).ifEmpty {
        listOf(point.veinPos)
      }

      val score = WaypointScorer.scoreWaypoint(
        playerPos = player.position(),
        waypoint = point.standPos
      ).totalCost - 30.0

      targets += TunnelTarget(
        ore = selectedOre,
        veinIndex = index,
        standPos = point.standPos,
        edgeBlock = point.veinPos,
        veinBlocks = veinBlocks,
        score = score,
        source = TargetSource.ROUTE_STORE,
        routeName = loaded.routeName,
        routeIndex = index,
        routePointType = point.pointType,
        routeBlockId = point.blockId
      )

      if (targets.size >= maxTargets) break
    }

    return targets.sortedBy { it.score }.take(maxTargets)
  }

  private fun scanRecordedData(
    selectedOres: Set<TunnelOreType>,
    maxTargets: Int
  ): List<TunnelTarget> {
    val targets = mutableListOf<TunnelTarget>()

    for (vein in TunnelVeinData.getVeins(selectedOres)) {
      if (!isVeinValid(vein.blocks, vein.ore)) continue

      val built = buildTargetsForVein(
        ore = vein.ore,
        veinIndex = vein.index,
        blocks = vein.blocks,
        source = TargetSource.RECORDED_DATA
      )

      targets += built

      if (targets.size >= maxTargets) break
    }

    return targets.sortedBy { it.score }.take(maxTargets)
  }

  private fun scanDynamicWorld(
    selectedOres: Set<TunnelOreType>,
    maxTargets: Int,
    radius: Int
  ): List<TunnelTarget> {
    val mc = Minecraft.getInstance()
    val player = mc.player ?: return emptyList()

    val center = player.blockPosition()
    val visited = hashSetOf<BlockPos>()
    val targets = mutableListOf<TunnelTarget>()
    var dynamicIndex = 0

    for (x in center.x - radius..center.x + radius) {
      for (y in center.y - radius..center.y + radius) {
        for (z in center.z - radius..center.z + radius) {
          val pos = BlockPos(x, y, z)

          if (visited.contains(pos)) continue

          val ore = oreAt(pos, selectedOres) ?: continue
          val vein = floodFillVein(pos, ore, selectedOres, visited)

          if (vein.size < 2) continue

          val built = buildTargetsForVein(
            ore = ore,
            veinIndex = dynamicIndex++,
            blocks = vein,
            source = TargetSource.DYNAMIC_SCAN
          )

          targets += built

          if (targets.size >= maxTargets) {
            return targets.sortedBy { it.score }.take(maxTargets)
          }
        }
      }
    }

    return targets.sortedBy { it.score }.take(maxTargets)
  }

  private fun floodFillVein(
    start: BlockPos,
    ore: TunnelOreType,
    selectedOres: Set<TunnelOreType>,
    visited: MutableSet<BlockPos>
  ): List<BlockPos> {
    val out = mutableListOf<BlockPos>()
    val queue: ArrayDeque<BlockPos> = ArrayDeque()

    queue.add(start)
    visited += start

    while (queue.isNotEmpty() && out.size < 96) {
      val pos = queue.removeFirst()

      val foundOre = oreAt(pos, selectedOres)
      if (foundOre != ore) continue

      out += pos

      for (offset in edgeOffsets) {
        val next = pos.offset(offset)

        if (visited.add(next)) {
          queue.add(next)
        }
      }
    }

    return out
  }

  private fun buildTargetsForVein(
    ore: TunnelOreType,
    veinIndex: Int,
    blocks: List<BlockPos>,
    source: TargetSource
  ): List<TunnelTarget> {
    val mc = Minecraft.getInstance()
    val player = mc.player ?: return emptyList()

    val veinBlocks = blocks.distinct()
    val veinSet = veinBlocks.toHashSet()
    val edgeBlocks = getEdgeBlocks(veinBlocks, veinSet)
    val targets = mutableListOf<TunnelTarget>()

    for (edge in edgeBlocks) {
      for (offset in horizontalOffsets) {
        val candidateStart = edge.offset(offset)
        val stand = findStandPosition(candidateStart, veinSet) ?: continue

        if (recentBadStandPositions.containsKey(stand)) continue

        val score = WaypointScorer.scoreWaypoint(
          playerPos = player.position(),
          waypoint = stand
        ).totalCost + sourcePenalty(source)

        targets += TunnelTarget(
          ore = ore,
          veinIndex = veinIndex,
          standPos = stand,
          edgeBlock = edge,
          veinBlocks = veinBlocks,
          score = score,
          source = source
        )
      }
    }

    return targets
  }

  private fun collectNearbyOreBlocks(
    center: BlockPos,
    ores: Set<TunnelOreType>,
    radius: Int
  ): List<BlockPos> {
    val out = mutableListOf<BlockPos>()

    for (x in center.x - radius..center.x + radius) {
      for (y in center.y - radius..center.y + radius) {
        for (z in center.z - radius..center.z + radius) {
          val pos = BlockPos(x, y, z)

          if (oreAt(pos, ores) != null) {
            out += pos
          }
        }
      }
    }

    return out
  }

  private fun sourcePenalty(source: TargetSource): Double {
    return when (source) {
      TargetSource.ROUTE_STORE_ORDERED -> -50.0
      TargetSource.ROUTE_STORE -> -30.0
      TargetSource.RECORDED_DATA -> 0.0
      TargetSource.DYNAMIC_SCAN -> 12.0
    }
  }

  private fun getEdgeBlocks(
    vein: List<BlockPos>,
    veinSet: Set<BlockPos>
  ): List<BlockPos> {
    val edges = mutableListOf<BlockPos>()

    for (block in vein) {
      val isEdge = edgeOffsets.any { offset ->
        !veinSet.contains(block.offset(offset))
      }

      if (isEdge) edges += block
    }

    return edges.distinct()
  }

  private fun findStandPosition(
    start: BlockPos,
    veinSet: Set<BlockPos>
  ): BlockPos? {
    if (veinSet.contains(start)) return null

    for (up in 0..2) {
      val raised = start.above(up)
      val stand = findStandOnColumn(raised, veinSet)

      if (stand != null) return stand
    }

    for (down in 1..4) {
      val lowered = start.below(down)
      val stand = findStandOnColumn(lowered, veinSet)

      if (stand != null) return stand
    }

    return null
  }

  private fun findStandOnColumn(
    pos: BlockPos,
    veinSet: Set<BlockPos>
  ): BlockPos? {
    if (veinSet.contains(pos)) return null

    val below = pos.below()

    if (isPassable(below)) return null
    if (!hasClearance(pos)) return null

    return pos
  }

  private fun hasClearance(pos: BlockPos): Boolean {
    for (dy in 0..2) {
      if (!isPassable(pos.above(dy))) return false
    }

    return true
  }

  private fun isVeinValid(
    blocks: List<BlockPos>,
    ore: TunnelOreType
  ): Boolean {
    if (blocks.isEmpty()) return false

    return blocks.any { pos ->
      oreAt(pos, setOf(ore)) == ore
    }
  }

  private fun oreAt(
    pos: BlockPos,
    selectedOres: Set<TunnelOreType>
  ): TunnelOreType? {
    val level = Minecraft.getInstance().level ?: return null
    val state = level.getBlockState(pos)
    val block = state.block

    if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR || block is AirBlock) {
      return null
    }

    val id = block.descriptionId.lowercase()
    val blockText = buildString {
      append(id)
      append(" ")
      append(block.toString().lowercase())
    }

    val searchOres = selectedOres.ifEmpty {
      TunnelOreType.entries.toSet()
    }

    return searchOres.firstOrNull { ore ->
      ore.keywords.any { keyword ->
        blockText.contains(keyword.lowercase())
      }
    }
  }

  private fun routeSizeFor(routeName: String): Int {
    return TunnelRouteStoreBridge.loadActiveTunnelRoute()?.points?.size ?: 1
  }

  private fun isPassable(pos: BlockPos): Boolean {
    val level = Minecraft.getInstance().level ?: return false
    val state = level.getBlockState(pos)
    val block = state.block

    if (block == Blocks.AIR) return true
    if (block == Blocks.CAVE_AIR) return true
    if (block == Blocks.VOID_AIR) return true
    if (block == Blocks.SNOW) return true

    return state.getCollisionShape(level, pos).isEmpty
  }
}