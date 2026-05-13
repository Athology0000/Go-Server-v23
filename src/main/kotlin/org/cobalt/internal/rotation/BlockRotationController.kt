package org.cobalt.internal.rotation

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class BlockRotationController {

    private val mc: Minecraft
        get() = Minecraft.getInstance()

    private var request: BlockRotationRequest? = null

    private var fromPoint: BlockAimPoint? = null
    private var toPoint: BlockAimPoint? = null

    private var startYaw: Float = 0f
    private var startPitch: Float = 0f
    private var targetYaw: Float = 0f
    private var targetPitch: Float = 0f

    private var tick: Int = 0

    // Real-time progress (seconds) — drives smooth per-frame interpolation.
    // tick is still incremented for spec-compatible debug output.
    private var elapsedSeconds: Double = 0.0
    private var lastFrameNanos: Long = 0L

    // Controller-owned smoothed rotation. The eased curve writes the *target*;
    // these two variables exponentially chase it, which guarantees continuous
    // velocity across retargets and absorbs any per-frame discontinuity.
    private var smoothedYaw: Float = 0f
    private var smoothedPitch: Float = 0f
    private var smoothingInitialized: Boolean = false

    /** Smoothing rate constant (1/seconds). Higher = snappier, lower = floatier. */
    var smoothingRate: Double = 28.0

    /**
     * Optional precision point set by the caller (e.g. mining macro) once the
     * destination block reveals a sub-block precision aim. The controller will
     * retarget pitch/yaw toward it after the main interpolation completes.
     */
    var precisionPoint: Vec3? = null
        private set

    /** Precision point received before we reached the block edge — applied on arrival. */
    private var pendingPrecisionPoint: Vec3? = null

    /** Set once the smoother has caught up to the initial random face aim. */
    private var arrivedAtBlockEdge: Boolean = false

    // Soft-release state: when set, the controller stops accepting new
    // targets and keeps smoothing toward the last one until it converges or
    // the budget runs out, then auto-cancels.
    private var softReleaseFrames: Int = 0
    private var softReleaseThresholdDeg: Float = 0.6f

    /**
     * Fallback block the macro should aim at if [toPoint] disappears before
     * the rotation reaches it (e.g. another nearby ore broke first). Used by
     * the debug renderer to draw the recovery line.
     */
    var nextFallbackBlock: BlockPos? = null
        private set

    var debugState: RotationDebugState = RotationDebugState()
        private set

    fun rotate(
        fromBlock: BlockPos,
        toBlock: BlockPos,
        durationTicks: Int = 8
    ) {
        rotate(
            BlockRotationRequest(
                fromBlock = fromBlock,
                toBlock = toBlock,
                durationTicks = durationTicks
            )
        )
    }

    fun rotate(newRequest: BlockRotationRequest) {
        if (softReleaseFrames > 0) return
        val player = mc.player ?: return

        request = newRequest
        tick = 0
        elapsedSeconds = 0.0
        lastFrameNanos = 0L
        // Reset smoothing rate to the default whenever a new segment begins.
        // Pathfinding's continuous mode adjusts this per frame; mining etc.
        // should always start fresh.
        smoothingRate = 28.0
        // Keep smoothingInitialized — preserves velocity continuity across retargets.
        precisionPoint = null
        pendingPrecisionPoint = null
        arrivedAtBlockEdge = false
        nextFallbackBlock = null

        fromPoint = BlockAimPointResolver.resolve(
            player = player,
            block = newRequest.fromBlock,
            faceHint = newRequest.fromFaceHint,
            offsetStrength = newRequest.aimOffsetStrength
        )

        toPoint = BlockAimPointResolver.resolve(
            player = player,
            block = newRequest.toBlock,
            faceHint = newRequest.toFaceHint,
            offsetStrength = newRequest.aimOffsetStrength
        )

        val eye = RotationMath.eyePos(player)

        val computedStart = RotationMath.rotationToPoint(
            playerEyePos = eye,
            target = fromPoint!!.point
        )

        val computedTarget = RotationMath.rotationToPoint(
            playerEyePos = eye,
            target = toPoint!!.point
        )

        if (newRequest.useFromBlockAsStartRotation) {
            startYaw = computedStart.yaw
            startPitch = computedStart.pitch
        } else {
            startYaw = player.yRot
            startPitch = player.xRot
        }

        targetYaw = computedTarget.yaw
        targetPitch = computedTarget.pitch

        // Seed the smoother from the player's *current* rotation so the next
        // frame doesn't jump. On a retarget mid-flight, smoothedYaw is already
        // tracking — keep its existing value so velocity stays continuous.
        if (!smoothingInitialized) {
            smoothedYaw = player.yRot
            smoothedPitch = player.xRot
            smoothingInitialized = true
        }

        debugState = debugSnapshot(active = true, finished = false)
    }

    /**
     * Continuous-target mode used by pathfinding. Call every tick/frame with
     * the live lookahead rotation. The eased curve is bypassed — the exp
     * smoother chases the raw target directly, so motion is one fluid sweep
     * across moving aim points instead of a sequence of discrete easings.
     *
     * Synthesizes a minimal request the first time it's called so [updateFrame]
     * keeps running for as long as the caller keeps feeding targets.
     */
    fun setDirectTarget(yaw: Float, pitch: Float) {
        if (softReleaseFrames > 0) return
        val player = mc.player ?: return

        if (request == null) {
            request = BlockRotationRequest(
                fromBlock = player.blockPosition(),
                toBlock = player.blockPosition(),
                durationTicks = 1,
                easing = RotationEasingType.LINEAR,
                aimOffsetStrength = 0.0,
            )
            if (!smoothingInitialized) {
                smoothedYaw = player.yRot
                smoothedPitch = player.xRot
                smoothingInitialized = true
            }
            arrivedAtBlockEdge = true
        }

        // Make the eased curve a no-op: collapse start to target and pin
        // progress past 1.0 so the curve always reads as `target`.
        startYaw = yaw
        startPitch = pitch
        targetYaw = yaw
        targetPitch = pitch
        elapsedSeconds = 1000.0
    }

    fun retargetTo(block: BlockPos, durationTicks: Int? = null) {
        val active = request ?: return
        rotate(
            active.copy(
                fromBlock = active.toBlock,
                toBlock = block,
                durationTicks = durationTicks ?: active.durationTicks
            )
        )
    }

    /**
     * Stage / apply a precision sub-block aim point.
     *
     *  - During the initial visible-face rotation: queue for application on arrival.
     *  - After arrival (or with no active request): immediately retarget toward
     *    the precision point. This handles the common race where the macro's
     *    precision tracker doesn't return a point until *after* the first
     *    rotation has already finished and the controller released itself.
     */
    fun setPrecisionPoint(point: Vec3?) {
        if (point == null) {
            precisionPoint = null
            pendingPrecisionPoint = null
            return
        }

        if (!arrivedAtBlockEdge && request != null) {
            // Still rotating toward the random face spot — stage for handoff.
            pendingPrecisionPoint = point
            return
        }

        val player = mc.player ?: run {
            pendingPrecisionPoint = point
            return
        }
        val eye = RotationMath.eyePos(player)
        val rot = RotationMath.rotationToPoint(eye, point)

        // Apply now. If there's no active request (controller already released
        // after reaching the face), synthesize one so update() keeps running.
        if (request == null) {
            val targetBlock = BlockPos.containing(point)
            request = BlockRotationRequest(
                fromBlock = targetBlock,
                toBlock = targetBlock,
                durationTicks = 6,
                easing = RotationEasingType.EASE_IN_OUT_SINE,
                aimOffsetStrength = 0.0,
            )
            arrivedAtBlockEdge = true
            if (!smoothingInitialized) {
                smoothedYaw = player.yRot
                smoothedPitch = player.xRot
                smoothingInitialized = true
            }
        }

        precisionPoint = point
        pendingPrecisionPoint = null
        startYaw = player.yRot
        startPitch = player.xRot
        targetYaw = rot.yaw
        targetPitch = rot.pitch
        elapsedSeconds = 0.0
    }

    /** Returns true once the smoother has reached the block's visible-face aim. */
    fun hasArrivedAtBlockEdge(): Boolean = arrivedAtBlockEdge

    fun setFallbackBlock(block: BlockPos?) {
        nextFallbackBlock = block
    }

    /** Tick-based update (kept for spec compatibility). */
    fun tick() {
        // Synthetic 50 ms step; render-frame [update] is what actually drives
        // motion now, but anything calling tick() (legacy) still progresses.
        update(0.05)
    }

    /**
     * Frame-driven update. Call once per render frame with the elapsed delta-time
     * since the previous frame in seconds. Motion is interpolated continuously
     * from start → target rotation using the request's easing curve.
     *
     * This is what makes mining/pathfinding rotations feel like a sweeping
     * camera move rather than a 20 Hz tick-stepped jerk.
     */
    fun updateFrame() {
        val now = System.nanoTime()
        val dt = if (lastFrameNanos == 0L) {
            1.0 / 60.0
        } else {
            ((now - lastFrameNanos).toDouble() / 1_000_000_000.0).coerceIn(1.0 / 240.0, 0.1)
        }
        lastFrameNanos = now
        update(dt)
    }

    private fun update(dt: Double) {
        val player = mc.player ?: return
        val activeRequest = request ?: return

        val durationSeconds = (activeRequest.durationTicks.coerceAtLeast(1).toDouble() / 20.0)

        elapsedSeconds += dt
        val progress = (elapsedSeconds / durationSeconds).coerceIn(0.0, 1.0)
        val eased = RotationEasing.apply(activeRequest.easing, progress)

        // Eased curve = the "ideal" position along the segment.
        val curveYaw = RotationMath.lerpAngleDegrees(startYaw, targetYaw, eased)
        val curvePitch = RotationMath.lerpFloat(startPitch, targetPitch, eased)
            .coerceIn(-90f, 90f)

        // Critically-damped exp-smoother chases the curve. blend = 1 - exp(-k*dt)
        // is the frame-rate-independent low-pass that absorbs any retarget jump
        // and produces continuous velocity even when start/target change mid-flight.
        val blend = (1.0 - kotlin.math.exp(-smoothingRate * dt))
            .coerceIn(0.0, 1.0).toFloat()

        val yawDelta = RotationMath.wrapDegrees(curveYaw - smoothedYaw)
        smoothedYaw = RotationMath.wrapDegrees(smoothedYaw + yawDelta * blend)
        smoothedPitch = (smoothedPitch + (curvePitch - smoothedPitch) * blend)
            .coerceIn(-90f, 90f)

        // Seed the *O ("previous render frame") fields from the value we wrote
        // last frame so MC's renderer interpolates from that anchor — no gap.
        player.yRotO = player.yRot
        player.xRotO = player.xRot
        player.yHeadRotO = player.yHeadRot
        player.yBodyRotO = player.yBodyRot

        player.setYRot(smoothedYaw)
        player.setXRot(smoothedPitch)
        player.yHeadRot = smoothedYaw
        player.yBodyRot = smoothedYaw

        tick = (elapsedSeconds * 20.0).toInt()
        // Settle test — finished only when the smoother has actually caught
        // up to the target. Otherwise short durationTicks would cut motion
        // off before the camera arrived, looking like a snap.
        val arrived = kotlin.math.abs(yawDelta) < 0.05f &&
            kotlin.math.abs(curvePitch - smoothedPitch) < 0.05f

        // Phase 1 → 2 transition. Once we've reached the random visible-face
        // aim, mark arrived and consume any precision point the macro staged
        // while we were still rotating in.
        if (!arrivedAtBlockEdge && progress >= 1.0 && arrived) {
            arrivedAtBlockEdge = true
            val staged = pendingPrecisionPoint
            if (staged != null) {
                pendingPrecisionPoint = null
                setPrecisionPoint(staged)
            }
        }

        val finished = progress >= 1.0 && arrived

        debugState = debugSnapshot(
            active = !finished,
            finished = finished
        )

        // Soft-release window: count down and auto-cancel once the smoother
        // has actually converged to the last target, OR the budget runs out.
        // Inputs are blocked while this is active so the camera lands cleanly.
        if (softReleaseFrames > 0) {
            softReleaseFrames--
            val settled = kotlin.math.abs(yawDelta) < softReleaseThresholdDeg &&
                kotlin.math.abs(curvePitch - smoothedPitch) < softReleaseThresholdDeg
            if (settled || softReleaseFrames == 0) {
                cancel()
                return
            }
        }

        // Do NOT auto-release here. After arrival the controller keeps
        // writing the smoothed rotation each frame, holding the camera
        // locked on the target until either:
        //   - another macro call invokes rotate() with a new target, or
        //   - cancel() is called explicitly (stopMacro).
        // Releasing on arrival caused mining LOS to drift because nothing
        // else held the rotation.
    }

    /**
     * Soft-release the controller. New [setDirectTarget] / [rotate] calls are
     * ignored while soft-releasing; the smoother keeps converging to the last
     * target for up to [maxFrames] frames, then auto-cancels. Prevents the
     * "clamp" feel where canceling mid-flight froze the camera short of aim.
     */
    fun releaseWhenSettled(maxFrames: Int = 18) {
        if (request == null) return
        softReleaseFrames = maxFrames.coerceAtLeast(1)
    }

    /** Whether a soft-release is currently in progress. */
    fun isSoftReleasing(): Boolean = softReleaseFrames > 0

    fun cancel() {
        request = null
        fromPoint = null
        toPoint = null
        precisionPoint = null
        pendingPrecisionPoint = null
        arrivedAtBlockEdge = false
        nextFallbackBlock = null
        tick = 0
        elapsedSeconds = 0.0
        lastFrameNanos = 0L
        smoothingInitialized = false
        softReleaseFrames = 0
        debugState = RotationDebugState(active = false, finished = true)
    }

    fun isActive(): Boolean {
        return request != null
    }

    fun currentRequest(): BlockRotationRequest? = request
    fun currentFromPoint(): BlockAimPoint? = fromPoint
    fun currentToPoint(): BlockAimPoint? = toPoint

    private fun finish() {
        request = null
        fromPoint = null
        toPoint = null
        precisionPoint = null
        pendingPrecisionPoint = null
        arrivedAtBlockEdge = false
        nextFallbackBlock = null
        tick = 0
        elapsedSeconds = 0.0
        lastFrameNanos = 0L
        smoothingInitialized = false
    }

    private fun debugSnapshot(active: Boolean, finished: Boolean): RotationDebugState {
        val player = mc.player

        val activeRequest = request

        val duration = activeRequest?.durationTicks ?: 0
        val rawProgress = if (duration <= 0) 0.0 else tick.toDouble() / duration.toDouble()
        val progress = rawProgress.coerceIn(0.0, 1.0)
        val eased = activeRequest?.let { RotationEasing.apply(it.easing, progress) } ?: 0.0

        return RotationDebugState(
            active = active,
            fromBlock = activeRequest?.fromBlock,
            toBlock = activeRequest?.toBlock,
            fromPoint = fromPoint?.point,
            toPoint = toPoint?.point,
            startYaw = startYaw,
            startPitch = startPitch,
            targetYaw = targetYaw,
            targetPitch = targetPitch,
            currentYaw = player?.yRot ?: 0f,
            currentPitch = player?.xRot ?: 0f,
            tick = tick,
            durationTicks = duration,
            progress = progress,
            easedProgress = eased,
            finished = finished,
            precisionPoint = precisionPoint,
            nextFallbackBlock = nextFallbackBlock
        )
    }
}
