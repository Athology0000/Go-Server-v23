package org.phantom.internal.rotation

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import kotlin.math.abs
import kotlin.random.Random

object BlockAimPointResolver {

    fun resolve(
        player: Player,
        block: BlockPos,
        faceHint: AimFaceHint,
        offsetStrength: Double
    ): BlockAimPoint {
        val center = Vec3(
            block.x + 0.5,
            block.y + 0.5,
            block.z + 0.5
        )

        val point = when (faceHint) {
            AimFaceHint.CENTER -> center

            AimFaceHint.NORTH_FACE -> center.add(randomOffset(offsetStrength).x, randomOffset(offsetStrength).y, -0.501)
            AimFaceHint.SOUTH_FACE -> center.add(randomOffset(offsetStrength).x, randomOffset(offsetStrength).y, 0.501)
            AimFaceHint.EAST_FACE -> center.add(0.501, randomOffset(offsetStrength).y, randomOffset(offsetStrength).z)
            AimFaceHint.WEST_FACE -> center.add(-0.501, randomOffset(offsetStrength).y, randomOffset(offsetStrength).z)
            AimFaceHint.TOP_FACE -> center.add(randomOffset(offsetStrength).x, 0.501, randomOffset(offsetStrength).z)
            AimFaceHint.BOTTOM_FACE -> center.add(randomOffset(offsetStrength).x, -0.501, randomOffset(offsetStrength).z)

            AimFaceHint.VISIBLE_FACE -> {
                resolveVisibleFacePoint(player, block, center, offsetStrength)
            }
        }

        return BlockAimPoint(
            block = block,
            point = point,
            faceHint = faceHint
        )
    }

    private fun resolveVisibleFacePoint(
        player: Player,
        block: BlockPos,
        center: Vec3,
        offsetStrength: Double
    ): Vec3 {
        val eye = Vec3(player.x, player.eyeY, player.z)
        val dir = center.subtract(eye)

        val ax = abs(dir.x)
        val ay = abs(dir.y)
        val az = abs(dir.z)

        val offset = randomOffset(offsetStrength)

        return when {
            ax >= ay && ax >= az -> {
                if (dir.x > 0.0) {
                    center.add(-0.501, offset.y, offset.z)
                } else {
                    center.add(0.501, offset.y, offset.z)
                }
            }

            ay >= ax && ay >= az -> {
                if (dir.y > 0.0) {
                    center.add(offset.x, -0.501, offset.z)
                } else {
                    center.add(offset.x, 0.501, offset.z)
                }
            }

            else -> {
                if (dir.z > 0.0) {
                    center.add(offset.x, offset.y, -0.501)
                } else {
                    center.add(offset.x, offset.y, 0.501)
                }
            }
        }
    }

    private fun randomOffset(strength: Double): Vec3 {
        val s = strength.coerceIn(0.0, 0.45)
        if (s <= 0.0) return Vec3.ZERO

        return Vec3(
            Random.nextDouble(-s, s),
            Random.nextDouble(-s, s),
            Random.nextDouble(-s, s)
        )
    }
}
