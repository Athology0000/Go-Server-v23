package org.phantom.api.module

import java.util.concurrent.ConcurrentHashMap
import org.phantom.api.util.ChatUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ModuleDebug {

  private val loggers = ConcurrentHashMap<String, Logger>()

  @JvmStatic
  fun isEnabled(moduleName: String): Boolean {
    return ModuleManager.getModules().firstOrNull { it.name == moduleName }?.isDebugEnabled() == true
  }

  @JvmStatic
  fun log(module: Module, message: String): Boolean {
    return log(module.name, message)
  }

  @JvmStatic
  fun log(moduleName: String, message: String): Boolean {
    if (!isEnabled(moduleName)) return false

    logger(moduleName).info(message)
    ChatUtils.sendDebug(moduleName, message)
    return true
  }

  @JvmStatic
  fun warn(module: Module, message: String): Boolean {
    if (!module.isDebugEnabled()) return false

    logger(module.name).warn(message)
    ChatUtils.sendDebug(module.name, "WARN: $message")
    return true
  }

  @JvmStatic
  fun error(module: Module, message: String): Boolean {
    if (!module.isDebugEnabled()) return false

    logger(module.name).error(message)
    ChatUtils.sendDebug(module.name, "ERROR: $message")
    return true
  }

  @JvmStatic
  fun logger(moduleName: String): Logger {
    return loggers.computeIfAbsent(moduleName) {
      LoggerFactory.getLogger("Phantom/Module/${it.replace(' ', '-')}")
    }
  }

}
