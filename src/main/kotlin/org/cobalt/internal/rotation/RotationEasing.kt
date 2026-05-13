package org.cobalt.internal.rotation

import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.pow

enum class RotationEasingType {
    LINEAR,
    SMOOTHSTEP,
    EASE_IN_OUT_SINE,
    EASE_OUT_CUBIC
}

object RotationEasing {

    fun apply(type: RotationEasingType, tRaw: Double): Double {
        val t = tRaw.coerceIn(0.0, 1.0)

        return when (type) {
            RotationEasingType.LINEAR -> t

            RotationEasingType.SMOOTHSTEP -> {
                t * t * (3.0 - 2.0 * t)
            }

            RotationEasingType.EASE_IN_OUT_SINE -> {
                -(cos(PI * t) - 1.0) / 2.0
            }

            RotationEasingType.EASE_OUT_CUBIC -> {
                1.0 - (1.0 - t).pow(3.0)
            }
        }
    }
}
