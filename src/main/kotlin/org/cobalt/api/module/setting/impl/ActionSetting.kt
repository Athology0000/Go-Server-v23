package org.cobalt.api.module.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.api.module.setting.Setting

/**
 * Action button setting. Not persisted; used to trigger an action from the UI.
 * If [buttonLabelProvider] is non-null, the button label is computed dynamically each frame.
 */
class ActionSetting(
  name: String,
  description: String,
  staticLabel: String,
  buttonLabelProvider: (() -> String)? = null,
  onClick: () -> Unit,
) : Setting<Boolean>(name, description, false) {

  private val _staticLabel = staticLabel
  private val _buttonLabelProvider = buttonLabelProvider
  private val _onClick = onClick
  val buttonLabel: String get() = _buttonLabelProvider?.invoke() ?: _staticLabel

  override val defaultValue: Boolean = false

  override fun read(element: JsonElement) {
    // No persisted state.
  }

  override fun write(): JsonElement {
    return JsonPrimitive(false)
  }

  fun trigger() {
    _onClick()
  }
}
