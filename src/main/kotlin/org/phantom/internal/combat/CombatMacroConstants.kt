package org.phantom.internal.combat

import java.awt.Color
import net.minecraft.core.BlockPos

// Reference positions spread across the crypt, used only for proximity checks.
internal val CRYPT_PATROL_WAYPOINTS = listOf(
  BlockPos(-152, 57, -102), BlockPos(-133, 50, -101), BlockPos(-132, 45, -121),
  BlockPos(-129, 41, -134), BlockPos(-129, 41, -143), BlockPos(-119, 41, -136),
  BlockPos(-106, 46, -129), BlockPos(-102, 48, -119), BlockPos(-89, 46, -104),
  BlockPos(-77, 46, -105),  BlockPos(-65, 50, -120),  BlockPos(-48, 55, -136),
  BlockPos(-48, 57, -141),  BlockPos(-46, 55, -136),  BlockPos(-80, 46, -101),
  BlockPos(-88, 42, -88),   BlockPos(-96, 38, -86),   BlockPos(-106, 38, -87),
  BlockPos(-114, 43, -89),  BlockPos(-133, 50, -105), BlockPos(-147, 56, -100),
  BlockPos(-145, 57, -114), BlockPos(-136, 58, -127), BlockPos(-121, 55, -126),
  BlockPos(-109, 50, -119), BlockPos(-102, 48, -119),
)

internal val CYAN_COLOR = Color(0, 255, 255)
internal val PINK_COLOR = Color(255, 105, 180)
internal val SPIDER_HATCHLING_TARGET_COLOR = Color(255, 180, 30, 210)
