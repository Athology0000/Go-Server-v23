package org.cobalt.internal.rotation

import java.awt.Color
import net.minecraft.client.Minecraft
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.cobalt.api.event.annotation.SubscribeEvent
import org.cobalt.api.event.impl.render.WorldRenderEvent
import org.cobalt.api.util.render.Render3D
import org.cobalt.internal.mining.MiningMacroModule
import org.cobalt.internal.mining.resolveMiningAimPoint
import org.cobalt.internal.mining.resolvePreviewTarget

/**
 * In-world mining rotation preview: current cursor hit -> active aim dot, plus
 * active aim dot -> next aim dot when the mining macro already knows the next block.
 */
object BlockRotationDebugRenderer {

    private val mc: Minecraft get() = Minecraft.getInstance()

    @SubscribeEvent
    fun onRender(event: WorldRenderEvent.Last) {
        val miningDebug = MiningMacroModule.shouldRenderRotationDebug()
        if (!RotationsModule.blockRotationDebug.value && !miningDebug) return

        val controller = CobaltRotation.blockController
        val player = mc.player ?: return
        val level = mc.level ?: return

        val aimEnd: Vec3 = controller.precisionPoint
            ?: controller.currentToPoint()?.point
            ?: controller.nextFallbackBlock?.let { Vec3(it.x + 0.5, it.y + 0.5, it.z + 0.5) }
            ?: MiningMacroModule.aimRenderPoint?.takeIf { miningDebug }
            ?: return

        val eye = Vec3(player.x, player.eyeY, player.z)
        val lookVec = player.lookAngle
        val reach = 6.0
        val end = eye.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach)
        val hit = level.clip(
            ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)
        )
        val currentLookPoint = if (hit.type == HitResult.Type.BLOCK) hit.location else end

        Render3D.drawLine(
            context = event.context,
            start = currentLookPoint,
            end = aimEnd,
            color = AIM_LINE,
            esp = true,
            thickness = 1.35f,
        )
        drawAimDot(event.context, aimEnd, AIM_DOT, AIM_DOT_FILL, 0.052)
        drawCursorCross(event.context, currentLookPoint)

        if (miningDebug) {
            val previewBlock = MiningMacroModule.resolvePreviewTarget(level, player)
            val currentBlock = MiningMacroModule.aimRenderBlock
            if (previewBlock != null && previewBlock != currentBlock) {
                val previewPoint = MiningMacroModule.resolveMiningAimPoint(player, previewBlock).point
                Render3D.drawLine(
                    context = event.context,
                    start = aimEnd,
                    end = previewPoint,
                    color = NEXT_LINE,
                    esp = true,
                    thickness = 1.05f,
                )
                drawAimDot(event.context, previewPoint, NEXT_DOT, NEXT_DOT_FILL, 0.04)
            }
        }
    }

    private fun drawAimDot(
        context: org.cobalt.api.event.impl.render.WorldRenderContext,
        point: Vec3,
        stroke: Color,
        fill: Color,
        size: Double,
    ) {
        Render3D.drawStyledBox(
            context = context,
            box = AABB(
                point.x - size,
                point.y - size,
                point.z - size,
                point.x + size,
                point.y + size,
                point.z + size,
            ),
            strokeColor = stroke,
            fillColor = fill,
            esp = true,
            lineWidth = 1.6f,
        )
    }

    private fun drawCursorCross(
        context: org.cobalt.api.event.impl.render.WorldRenderContext,
        point: Vec3,
    ) {
        val arm = 0.055
        val gap = 0.018
        Render3D.drawLine(context, point.add(-arm, 0.0, 0.0), point.add(-gap, 0.0, 0.0), CURSOR_CROSS, true, 1.0f)
        Render3D.drawLine(context, point.add(gap, 0.0, 0.0), point.add(arm, 0.0, 0.0), CURSOR_CROSS, true, 1.0f)
        Render3D.drawLine(context, point.add(0.0, -arm, 0.0), point.add(0.0, -gap, 0.0), CURSOR_CROSS, true, 1.0f)
        Render3D.drawLine(context, point.add(0.0, gap, 0.0), point.add(0.0, arm, 0.0), CURSOR_CROSS, true, 1.0f)
        drawAimDot(context, point, CURSOR_CENTER, CURSOR_CENTER, 0.01)
    }

    private val AIM_DOT = Color(255, 0, 16, 255)
    private val AIM_DOT_FILL = Color(255, 0, 16, 230)
    private val AIM_LINE = Color(255, 0, 16, 205)
    private val NEXT_DOT = Color(255, 55, 55, 210)
    private val NEXT_DOT_FILL = Color(255, 55, 55, 140)
    private val NEXT_LINE = Color(255, 55, 55, 145)
    private val CURSOR_CROSS = Color(230, 230, 230, 235)
    private val CURSOR_CENTER = Color(255, 0, 16, 230)
}
