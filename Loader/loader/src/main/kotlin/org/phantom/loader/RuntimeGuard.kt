package org.phantom.loader

import java.lang.management.ManagementFactory

object RuntimeGuard {
    private val blockedArgs = listOf(
        "-javaagent",
        "-agentlib",
        "-agentpath",
        "-Xdebug",
        "-Xrunjdwp",
        "--patch-module",
        "-Xbootclasspath",
    )

    fun verify() {
        val offending = ManagementFactory.getRuntimeMXBean()
            .inputArguments
            .firstOrNull { arg -> blockedArgs.any { blocked -> arg.contains(blocked, ignoreCase = true) } }

        require(offending == null) {
            "Phantom refuses to start with debug/agent JVM argument: $offending"
        }
    }
}
