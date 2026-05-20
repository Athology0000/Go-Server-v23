package org.phantom.internal.loader

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.security.CodeSource
import java.security.ProtectionDomain
import java.util.jar.JarInputStream

internal class InMemoryAddonClassLoader(
  private val sourceName: String,
  jarBytes: ByteArray,
  parent: ClassLoader,
) : ClassLoader(parent) {

  private val entries: Map<String, ByteArray> = readEntries(jarBytes)
  private val protectionDomain = ProtectionDomain(CodeSource(null, emptyArray()), null, this, null)

  fun getEntryBytes(path: String): ByteArray? {
    return entries[path.trimStart('/')]
  }

  override fun findClass(name: String): Class<*> {
    val path = name.replace('.', '/') + ".class"
    val bytecode = entries[path] ?: throw ClassNotFoundException(name)

    definePackageIfNeeded(name)

    return defineClass(name, bytecode, 0, bytecode.size, protectionDomain)
  }

  override fun getResource(name: String): URL? {
    val normalized = name.trimStart('/')
    if (!entries.containsKey(normalized)) return super.getResource(name)

    return URL(null, "memory-addon:$sourceName!/$normalized", MemoryResourceHandler(entries[normalized]!!))
  }

  override fun getResourceAsStream(name: String): InputStream? {
    return entries[name.trimStart('/')]?.inputStream() ?: super.getResourceAsStream(name)
  }

  private fun definePackageIfNeeded(className: String) {
    val packageName = className.substringBeforeLast('.', "")
    if (packageName.isBlank() || getDefinedPackage(packageName) != null) return

    definePackage(packageName, null, null, null, null, null, null, null)
  }

  private class MemoryResourceHandler(
    private val bytes: ByteArray,
  ) : URLStreamHandler() {
    override fun openConnection(url: URL): URLConnection {
      return object : URLConnection(url) {
        override fun connect() {
        }

        override fun getInputStream(): InputStream {
          return ByteArrayInputStream(bytes)
        }
      }
    }
  }

  private companion object {
    private fun readEntries(jarBytes: ByteArray): Map<String, ByteArray> {
      val out = linkedMapOf<String, ByteArray>()

      JarInputStream(jarBytes.inputStream()).use { jar ->
        while (true) {
          val entry = jar.nextJarEntry ?: break
          if (!entry.isDirectory) {
            out[entry.name] = jar.readBytes()
          }
        }
      }

      return out
    }
  }
}
