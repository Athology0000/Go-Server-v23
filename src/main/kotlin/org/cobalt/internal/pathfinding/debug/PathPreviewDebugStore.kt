package org.cobalt.internal.pathfinding.debug

import net.minecraft.core.BlockPos
import org.cobalt.api.pathfinder.jni.ActionType
import org.cobalt.api.pathfinder.jni.PathStatus

data class PathPreviewNode(val pos: BlockPos, val action: ActionType, val cost: Double, val note: String)

object PathPreviewDebugStore {

  var source: String = ""
    private set
  var start: BlockPos? = null
    private set
  var target: BlockPos? = null
    private set
  var status: PathStatus = PathStatus.IDLE
    private set
  var failReason: String = ""
    private set
  val nodes: MutableList<PathPreviewNode> = mutableListOf()

  fun begin(start: BlockPos?, target: BlockPos, source: String) {
    this.start = start
    this.target = target
    this.source = source
    this.status = PathStatus.IDLE
    this.failReason = ""
    nodes.clear()
  }

  fun appendNode(pos: BlockPos, action: ActionType, cost: Double, note: String) {
    nodes.add(PathPreviewNode(pos, action, cost, note))
  }

  fun finish(status: PathStatus, failReason: String) {
    this.status = status
    this.failReason = failReason
  }
}
