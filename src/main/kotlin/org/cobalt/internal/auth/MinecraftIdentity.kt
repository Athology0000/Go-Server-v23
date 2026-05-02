package org.cobalt.internal.auth

object MinecraftIdentity {
  fun currentUsername(): String? {
    return try {
      val minecraftClass = Class.forName("net.minecraft.client.Minecraft")
      val getInstance = minecraftClass.getDeclaredMethod("getInstance")
      val client = getInstance.invoke(null)

      val userField = minecraftClass.getDeclaredField("user")
      userField.isAccessible = true

      val user = userField.get(client)
      if (user == null) {
        CobaltAuthDebug.warn("minecraft user object was null")
        return null
      }

      val nameMethod = user.javaClass.methods.firstOrNull {
        it.name == "getName" || it.name == "name"
      }

      val name = nameMethod?.invoke(user)?.toString()?.trim()

      CobaltAuthDebug.info("minecraft username detected=${name ?: "<none>"}")

      if (name.isNullOrBlank()) null else name
    } catch (t: Throwable) {
      CobaltAuthDebug.error("failed to read Minecraft username reflectively", t)
      null
    }
  }
}
