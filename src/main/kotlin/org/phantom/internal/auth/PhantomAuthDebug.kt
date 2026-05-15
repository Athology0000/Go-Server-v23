package org.phantom.internal.auth

object PhantomAuthDebug {
  private const val PREFIX = "[Phantom-Auth]"

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
