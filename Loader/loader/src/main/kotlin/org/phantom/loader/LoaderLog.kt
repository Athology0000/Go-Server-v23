package org.phantom.loader

object LoaderLog {
    fun info(message: String) = println("[Phantom] $message")
    fun warn(message: String) = println("[Phantom/WARN] $message")
    fun error(message: String, throwable: Throwable? = null) {
        println("[Phantom/ERROR] $message")
        throwable?.printStackTrace()
    }
}
