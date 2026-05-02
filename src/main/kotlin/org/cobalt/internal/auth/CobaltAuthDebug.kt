package org.cobalt.internal.auth

object CobaltAuthDebug {
  private const val PREFIX = "[Cobalt-Auth]"

  fun info(message: String) {
    println("$PREFIX $message")
  }

  fun warn(message: String) {
    println("$PREFIX WARN: $message")
  }

  fun error(message: String, throwable: Throwable? = null) {
    println("$PREFIX ERROR: $message")
    throwable?.printStackTrace()
  }
}
