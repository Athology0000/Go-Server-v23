package org.phantom.internal.debug

import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting

/**
 * Mod-wide debug master switch. When `masterDebug` is on, every
 * `module.debug { ... }` call across the mod fires (chat + slf4j) regardless of
 * the per-module debug toggle. Wired into `ModuleDebug.isEnabled`.
 */
object DebugModule : Module("Debug") {
    override val category = ModuleCategory.CORE

    val masterDebug = CheckboxSetting(
        "Master Debug",
        "Force every module's debug output on regardless of per-module debug toggle.",
        false,
    )

    init {
        addSetting(masterDebug)
    }
}
