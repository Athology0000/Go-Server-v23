package org.cobalt.internal.garden

import net.minecraft.client.Minecraft

object ScriptBridge {

    private val mc get() = Minecraft.getInstance()

    fun startFarming(script: String)       = send("ez-macrostart $script")
    fun startPestScript(script: String)    = send("ez-macrostart $script")
    fun startVisitorScript(script: String) = send("ez-macrostart $script")
    fun startReturnScript(script: String)  = send("ez-macrostart $script")
    fun stopScript()                       = send("ez-macrostop")
    fun setSpawn()                         = send("setspawn")
    fun warpGarden()                       = send("warp garden")

    private fun send(cmd: String) {
        mc.player?.connection?.sendCommand(cmd)
    }
}
