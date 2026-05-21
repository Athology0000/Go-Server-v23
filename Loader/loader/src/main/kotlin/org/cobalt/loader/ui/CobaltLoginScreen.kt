package org.cobalt.loader.ui

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import org.cobalt.loader.bootstrap.BootstrapStartResult
import org.cobalt.loader.bootstrap.BootstrapStarter
import org.cobalt.loader.config.SavedLoginConfig
import kotlin.concurrent.thread

class CobaltLoginScreen(
    private val previousScreen: Screen?,
    initialUsername: String
) : Screen(Text.literal("Cobalt Login")) {
    private var username = initialUsername
    private var status = Text.literal("Sign in with your Cobalt account username.")
    private var working = false
    private lateinit var usernameField: TextFieldWidget
    private lateinit var loginButton: ButtonWidget

    override fun init() {
        val panelWidth = 260
        val left = (width - panelWidth) / 2
        val top = height / 2 - 64

        usernameField = TextFieldWidget(textRenderer, left, top + 36, panelWidth, 20, Text.literal("Cobalt username"))
        usernameField.setMaxLength(64)
        usernameField.text = username
        usernameField.setPlaceholder(Text.literal("Cobalt username"))
        usernameField.setChangedListener { value -> username = value.trim() }
        addDrawableChild(usernameField)

        loginButton = ButtonWidget.builder(Text.literal("Login")) {
            submit()
        }.dimensions(left, top + 68, panelWidth, 20).build()
        addDrawableChild(loginButton)

        setInitialFocus(usernameField)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        renderBackground(context, mouseX, mouseY, deltaTicks)

        val centerX = width / 2
        val top = height / 2 - 64
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Cobalt"), centerX, top, 0xFFFFFF)
        context.drawCenteredTextWithShadow(textRenderer, status, centerX, top + 18, 0xD0D0D0)
        context.drawTextWithShadow(textRenderer, Text.literal("Username"), (width - 260) / 2, top + 25, 0xA0A0A0)

        super.render(context, mouseX, mouseY, deltaTicks)
    }

    override fun shouldCloseOnEsc(): Boolean = false

    private fun submit() {
        val requestedUsername = username.trim()
        if (requestedUsername.isBlank()) {
            status = Text.literal("Enter your Cobalt account username.")
            return
        }

        setWorking(true, "Authenticating...")
        SavedLoginConfig.saveUsername(requestedUsername)

        thread(name = "Cobalt-Bootstrap", isDaemon = true) {
            val result = BootstrapStarter.start(requestedUsername)
            MinecraftClient.getInstance().execute {
                when (result) {
                    BootstrapStartResult.Success -> {
                        status = Text.literal("Authenticated.")
                        MinecraftClient.getInstance().setScreen(previousScreen ?: TitleScreen())
                    }
                    is BootstrapStartResult.Failure -> {
                        val message = result.message.ifBlank { "Authentication failed." }
                        status = Text.literal(toUserMessage(message))
                        setWorking(false)
                    }
                }
            }
        }
    }

    private fun setWorking(value: Boolean, message: String? = null) {
        working = value
        if (::usernameField.isInitialized) {
            usernameField.active = !value
        }
        if (::loginButton.isInitialized) {
            loginButton.active = !value
        }
        if (message != null) {
            status = Text.literal(message)
        }
    }

    private fun toUserMessage(message: String): String {
        val lower = message.lowercase()
        return if ("minecraft" in lower && ("bound" in lower || "bind" in lower || "account" in lower)) {
            "This Minecraft account is not bound to that Cobalt account. Remove the bound account, then bind this one."
        } else {
            message.removePrefix("Auth failed: ")
        }
    }
}
