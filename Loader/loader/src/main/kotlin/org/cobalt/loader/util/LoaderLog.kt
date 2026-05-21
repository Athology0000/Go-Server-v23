package org.cobalt.loader.util

object LoaderLog {
    fun info(message: String) = println("[CobaltLoader] $message")
    fun warn(message: String) = println("[CobaltLoader/WARN] $message")
    fun error(message: String, throwable: Throwable? = null) {
        println("[CobaltLoader/ERROR] $message")
        throwable?.printStackTrace()
    }
}
