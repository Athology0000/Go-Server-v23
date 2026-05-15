package org.phantom.api.ui.theme

object ThemeSurface {

  fun panel(alpha: Int = 0xF0, theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.panel, alpha)

  fun panelSolid(theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.panel, 0xFF)

  fun panelGlass(theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.panel, 0x72)

  fun slotGlass(theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.panel, 0x50)

  fun inset(alpha: Int = 0xFF, theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.inset, alpha)

  fun track(theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.inset, 0xF0)

  fun overlay(alpha: Int = 0x14, theme: Theme = ThemeManager.currentTheme): Int =
    withAlpha(theme.white, alpha)

  fun withAlpha(color: Int, alpha: Int): Int =
    (alpha.coerceIn(0, 255) shl 24) or (color and 0x00FFFFFF)
}
