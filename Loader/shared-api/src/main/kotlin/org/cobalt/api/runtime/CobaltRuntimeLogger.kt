package org.cobalt.api.runtime

interface CobaltRuntimeLogger {
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String, throwable: Throwable? = null)
}
