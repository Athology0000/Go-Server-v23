package org.cobalt.api.util.render

import net.minecraft.world.phys.Vec3
import org.cobalt.render.rise.ShaderRegistry

object WorldGlowEngine {

    @JvmStatic
    fun addLine(start: Vec3, end: Vec3, r: Float, g: Float, b: Float, a: Float, cam: Vec3) {
        ShaderRegistry.WORLD_GLOW_PASS.addLine(
            (start.x - cam.x).toFloat(), (start.y - cam.y).toFloat(), (start.z - cam.z).toFloat(),
            (end.x   - cam.x).toFloat(), (end.y   - cam.y).toFloat(), (end.z   - cam.z).toFloat(),
            r, g, b, a,
        )
    }
}
