package org.cobalt.internal.auth

import net.minecraft.client.Minecraft

object MinecraftIdentity {
  fun currentUsername(): String? {
    return try {
      val name = Minecraft.getInstance().user.name.trim()
      CobaltAuthDebug.info("minecraft username detected=${name.ifBlank { "<none>" }}")
      if (name.isBlank()) null else name
    } catch (t: Throwable) {
      CobaltAuthDebug.error("failed to read Minecraft username", t)
      null
    }
  }
}
