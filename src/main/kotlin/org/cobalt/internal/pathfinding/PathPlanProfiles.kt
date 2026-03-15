package org.cobalt.internal.pathfinding

import org.cobalt.api.pathfinder.pathfinder.AStarPathfinder
import org.cobalt.api.pathfinder.pathing.heuristic.HeuristicWeights
import org.cobalt.api.pathfinder.pathing.heuristic.LinearHeuristicStrategy
import org.cobalt.api.pathfinder.pathing.minecraft.MinecraftParkourNeighborStrategy
import org.cobalt.api.pathfinder.pathing.processing.impl.AvoidanceProcessor
import org.cobalt.api.pathfinder.pathing.processing.impl.MinecraftParkourProcessor
import org.cobalt.api.pathfinder.pathing.processing.impl.MinecraftPathProcessor
import org.cobalt.api.pathfinder.provider.impl.MinecraftNavigationProvider

object PathPlanProfiles {

  const val DEFAULT_ID = "default"
  const val DEEP_ID = "deep"
  const val COMBAT_ID = "combat"

  val DEFAULT: PathPlanProfile =
    PathPlanProfile(
      id = DEFAULT_ID,
      maxIterations = 9000,
      maxLength = 0,
      async = false,
      fallback = true,
      providerFactory = { MinecraftNavigationProvider() },
      neighborStrategy = MinecraftParkourNeighborStrategy,
      heuristicWeights = HeuristicWeights(1.0, 0.0, 0.0, 0.5),
      heuristicStrategy = LinearHeuristicStrategy(),
      processorFactories = listOf(
        { AvoidanceProcessor() },
        { MinecraftParkourProcessor() },
        { MinecraftPathProcessor() }
      ),
      pathfinderFactory = { config -> AStarPathfinder(config) }
    )

  val DEEP: PathPlanProfile =
    PathPlanProfile(
      id = DEEP_ID,
      maxIterations = 18000,
      maxLength = 0,
      async = false,
      fallback = false,
      providerFactory = { MinecraftNavigationProvider() },
      neighborStrategy = MinecraftParkourNeighborStrategy,
      heuristicWeights = HeuristicWeights(1.0, 0.0, 0.0, 0.5),
      heuristicStrategy = LinearHeuristicStrategy(),
      processorFactories = listOf(
        { AvoidanceProcessor() },
        { MinecraftParkourProcessor() },
        { MinecraftPathProcessor() }
      ),
      pathfinderFactory = { config -> AStarPathfinder(config) }
    )

  val COMBAT: PathPlanProfile =
    PathPlanProfile(
      id = COMBAT_ID,
      maxIterations = 9000,
      maxLength = 0,
      async = false,
      fallback = false,
      providerFactory = { MinecraftNavigationProvider() },
      neighborStrategy = MinecraftParkourNeighborStrategy,
      heuristicWeights = HeuristicWeights(1.0, 0.0, 0.0, 0.5),
      heuristicStrategy = LinearHeuristicStrategy(),
      processorFactories = listOf(
        { AvoidanceProcessor() },
        { MinecraftParkourProcessor() },
        { MinecraftPathProcessor() }
      ),
      pathfinderFactory = { config -> AStarPathfinder(config) }
    )
}
