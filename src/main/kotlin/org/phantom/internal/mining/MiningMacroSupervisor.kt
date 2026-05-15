package org.phantom.internal.mining

/**
 * Central ownership lock for mining-related automation.
 *
 * This keeps Commission, Gemstone, Tunnel, Powder, Ore, and other mining macros
 * from fighting over movement, rotation, and block breaking at the same time.
 */
object MiningMacroSupervisor {
  private var owner: String? = null

  val activeOwner: String?
    get() = owner

  val isBusy: Boolean
    get() = owner != null

  fun acquire(requester: String): Boolean {
    val current = owner
    if (current == null || current == requester) {
      owner = requester
      return true
    }

    return false
  }

  fun release(requester: String) {
    if (owner == requester) {
      owner = null
    }
  }

  fun forceRelease() {
    owner = null
  }

  fun owns(requester: String): Boolean {
    return owner == requester
  }
}
