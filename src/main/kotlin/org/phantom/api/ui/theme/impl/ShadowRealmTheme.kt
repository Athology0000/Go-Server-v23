package org.phantom.api.ui.theme.impl

import java.awt.Color
import org.phantom.api.ui.theme.Theme

/**
 * Dark purple "Shadow Realm" theme inspired by the yugui palette:
 * - bg: #050008
 * - text: #e0d8f8
 * - accent: #9b59ff
 * - accent2: #c084fc
 */
class ShadowRealmTheme : Theme {

  override val name = "Shadow Realm"

  override val rainbowEnabled = false
  override val rainbowSpeed = 1f
  override val rainbowSaturation = 1f
  override val rainbowBrightness = 1f
  override val chatGradient = ""

  // Base
  override val background = Color(0x05, 0x00, 0x08).rgb
  override val panel = Color(0x0D, 0x08, 0x0F).rgb
  override val inset = Color(0x13, 0x0E, 0x16).rgb
  override val overlay = Color(0x05, 0x00, 0x08, 230).rgb

  // Text
  override val text = Color(0xE0, 0xD8, 0xF8).rgb
  override val textPrimary = Color(0xF3, 0xEF, 0xFF).rgb
  override val textSecondary = Color(0xE0, 0xD8, 0xF8, (0.45f * 255).toInt()).rgb
  override val textDisabled = Color(0xE0, 0xD8, 0xF8, (0.18f * 255).toInt()).rgb
  override val textPlaceholder = Color(0xE0, 0xD8, 0xF8, (0.18f * 255).toInt()).rgb
  override val textOnAccent = Color(0xFF, 0xFF, 0xFF).rgb

  // Accent
  override val accent = Color(0x9B, 0x59, 0xFF).rgb
  override val accentPrimary = Color(0x7A, 0x30, 0xE0).rgb
  override val accentSecondary = Color(0xC0, 0x84, 0xFC).rgb
  override val selection = Color(0x9B, 0x59, 0xFF, 100).rgb

  // Controls (subtle glass-like surfaces and purple borders)
  override val controlBg = Color(0x13, 0x0E, 0x16, 190).rgb
  override val controlBorder = Color(0x9B, 0x59, 0xFF, (0.14f * 255).toInt()).rgb
  override val inputBg = Color(0x13, 0x0E, 0x16, 205).rgb
  override val inputBorder = Color(0x9B, 0x59, 0xFF, (0.28f * 255).toInt()).rgb

  // Status
  override val success = Color(0x00, 0xFF, 0xA3).rgb
  override val warning = Color(0xFF, 0xAA, 0x00).rgb
  override val error = Color(0xFF, 0x38, 0x60).rgb
  override val info = Color(0x4D, 0xC8, 0xFF).rgb

  // Scroll/slider
  override val scrollbarThumb = accent
  override val scrollbarTrack = inset
  override val sliderTrack = Color(0x9B, 0x59, 0xFF, (0.14f * 255).toInt()).rgb
  override val sliderFill = accent
  override val sliderThumb = accentSecondary

  // Tooltip
  override val tooltipBackground = Color(0x05, 0x00, 0x08, 240).rgb
  override val tooltipBorder = Color(0x9B, 0x59, 0xFF, (0.28f * 255).toInt()).rgb
  override val tooltipText = text

  // Notifications
  override val notificationBackground = background
  override val notificationBorder = accent
  override val notificationText = text
  override val notificationTextSecondary = textSecondary

  // Status backgrounds/borders/icons
  override val infoBackground = Color(0x4D, 0xC8, 0xFF, 25).rgb
  override val infoBorder = Color(0x4D, 0xC8, 0xFF, 150).rgb
  override val infoIcon = Color(0x4D, 0xC8, 0xFF).rgb

  override val warningBackground = Color(0xFF, 0xAA, 0x00, 25).rgb
  override val warningBorder = Color(0xFF, 0xAA, 0x00, 150).rgb
  override val warningIcon = Color(0xFF, 0xAA, 0x00).rgb

  override val successBackground = Color(0x00, 0xFF, 0xA3, 25).rgb
  override val successBorder = Color(0x00, 0xFF, 0xA3, 150).rgb
  override val successIcon = Color(0x00, 0xFF, 0xA3).rgb

  override val errorBackground = Color(0xFF, 0x38, 0x60, 25).rgb
  override val errorBorder = Color(0xFF, 0x38, 0x60, 150).rgb
  override val errorIcon = Color(0xFF, 0x38, 0x60).rgb

  // Misc UI
  override val selectionText = textPrimary
  override val searchPlaceholderText = textDisabled
  override val moduleDivider = Color(0x9B, 0x59, 0xFF, (0.14f * 255).toInt()).rgb
  override val selectedOverlay = Color(0x9B, 0x59, 0xFF, 50).rgb

  override val white = Color(255, 255, 255).rgb
  override val black = Color(0, 0, 0).rgb
  override val transparent = Color(0, 0, 0, 0).rgb
}

