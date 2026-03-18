package org.cobalt.internal.garden

import net.minecraft.client.Minecraft

object ScriptBridge {

    private val mc get() = Minecraft.getInstance()

    // Taunahi scripts — sent as chat messages (Taunahi intercepts .ez- prefix)
    fun startFarming(script: String)       = sendChat(".ez-startscript $script")
    fun startPestScript(script: String)    = sendChat(".ez-startscript $script")
    fun startVisitorScript(script: String) = sendChat(".ez-startscript $script")
    fun startReturnScript(script: String)  = sendChat(".ez-startscript $script")

    fun stopScript() {
        // Release held keys before stopping (mirrors ihanuat behaviour)
        mc.execute {
            mc.options.keyUp.isDown    = false
            mc.options.keyDown.isDown  = false
            mc.options.keyLeft.isDown  = false
            mc.options.keyRight.isDown = false
            mc.options.keyJump.isDown  = false
            mc.options.keyShift.isDown = false
        }
        sendChat(".ez-stopscript")
    }

    // Server slash commands
    fun setSpawn()    = sendCommand("setspawn")
    fun warpGarden()  = sendCommand("warp garden")

    private fun sendChat(msg: String) {
        mc.execute { mc.player?.connection?.sendChat(msg) }
    }

    private fun sendCommand(cmd: String) {
        mc.execute { mc.player?.connection?.sendCommand(cmd) }
    }
}
