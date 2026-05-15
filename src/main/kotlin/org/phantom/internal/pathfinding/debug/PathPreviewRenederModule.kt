package org.phantom.internal.pathfinding.debug

import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.InfoSetting
import org.phantom.api.module.setting.impl.InfoType

object PathPreviewRenderModule : Module("Path Preview Render") {

  override val category = ModuleCategory.MINING

  private val enabled = CheckboxSetting(
    "Enabled",
    "Keep the path preview render module registered. Rendering is disabled until the world-render API is wired for this Minecraft/Fabric version.",
    false
  )

  private val info = InfoSetting(
    "Info",
    "Compile-safe shell. The command/debug store still work; world rendering can be re-added with the correct render event imports.",
    InfoType.INFO
  )

  init {
    addSetting(enabled, info)
  }
}
