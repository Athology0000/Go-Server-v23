package org.cobalt.api.pathfinder.jni

/**
 * Tunable knobs read each frame by [PathRotationStrategy]. Written by
 * PathfindingModule from its setting `onChange` callbacks, same pattern as
 * the lookahead settings on [PathExecutorState].
 */
object PathRotationConfig {
    @JvmField var baseYawRate: Double = 9.0
    @JvmField var basePitchRate: Double = 14.0
    @JvmField var cornerAnticipation: Double = 5.0
    @JvmField var verticalAnticipation: Double = 6.0
    @JvmField var humanizationDrift: Double = 0.0
}
