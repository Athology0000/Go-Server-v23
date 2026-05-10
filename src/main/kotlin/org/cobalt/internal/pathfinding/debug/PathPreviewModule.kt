package org.cobalt.internal.pathfinding.debug

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import org.cobalt.api.pathfinder.PathOwner
import org.cobalt.api.pathfinder.jni.ActionType
import org.cobalt.api.pathfinder.jni.PathStatus
import org.cobalt.api.util.ChatUtils
import org.cobalt.internal.pathfinding.PathfindingModule

object PathPreviewModule {

  fun createPreviewTo(
    target: BlockPos,
    source: String = "manual",
    startRealPath: Boolean = false
  ) {
    val mc = Minecraft.getInstance()
    val player = mc.player
    val start = player?.blockPosition()

    PathPreviewDebugStore.begin(
      start = start,
      target = target,
      source = source
    )

    if (start != null) {
      PathPreviewDebugStore.appendNode(
        pos = start,
        action = ActionType.WALK,
        cost = 0.0,
        note = "start"
      )
    }

    PathPreviewDebugStore.appendNode(
      pos = target,
      action = ActionType.WALK,
      cost = start?.distManhattan(target)?.toDouble() ?: 0.0,
      note = "target"
    )

    if (startRealPath) {
      val accepted = PathfindingModule.startTo(
        x = target.x.toDouble(),
        y = target.y.toDouble(),
        z = target.z.toDouble(),
        owner = PathOwner.USER,
        source = "PathPreview:$source"
      )

      PathPreviewDebugStore.finish(
        status = if (accepted) PathStatus.EXECUTING else PathStatus.FAILED,
        failReason = if (accepted) "" else "path request rejected"
      )

      ChatUtils.sendMessage(
        if (accepted) "Path Preview: started path to ${target.x}, ${target.y}, ${target.z}."
        else "Path Preview: path request rejected."
      )
    } else {
      PathPreviewDebugStore.finish(
        status = PathStatus.EXECUTING,
        failReason = ""
      )

      ChatUtils.sendMessage("Path Preview: created preview to ${target.x}, ${target.y}, ${target.z}.")
    }
  }
}
