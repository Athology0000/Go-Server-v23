package org.phantom.internal.dungeons.autoroutes

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.awt.Color
import java.io.File
import kotlin.math.sqrt
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ambient.Bat
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.client.PacketEvent
import org.phantom.api.event.impl.client.TickEvent
import org.phantom.api.event.impl.render.WorldRenderEvent
import org.phantom.api.module.Module
import org.phantom.api.module.ModuleCategory
import org.phantom.api.module.setting.impl.CheckboxSetting
import org.phantom.api.module.setting.impl.ColorSetting
import org.phantom.api.module.setting.impl.KeyBindSetting
import org.phantom.api.module.setting.impl.SliderSetting
import org.phantom.api.util.AngleUtils
import org.phantom.api.util.ChatUtils
import org.phantom.api.util.TickScheduler
import org.phantom.api.util.helper.KeyBind
import org.phantom.api.util.render.Render3D
import org.phantom.internal.dungeons.map.DungeonRoom
import org.phantom.internal.dungeons.map.DungeonScanState
import org.phantom.internal.dungeons.map.RoomDirection

object AutoRoutesModule : Module("Auto Routes") {

  override val category = ModuleCategory.COMBAT

  private val mc = Minecraft.getInstance()
  private val routesFile: File by lazy { File(mc.gameDirectory, "config/phantom/autoroutes/routes.json") }

  private val enabled = CheckboxSetting("Enabled", "Load and run RSA-style dungeon autoroutes.", false)
  private val executeNodes = CheckboxSetting("Execute Nodes", "Trigger node actions when you enter their radius.", true)
  private val renderNodes = CheckboxSetting("Render Nodes", "Render autoroute nodes for the current room.", true)
  private val chatMessages = CheckboxSetting("Chat Messages", "Show route load and trigger messages.", true)
  private val centerOnly = CheckboxSetting("Center Only", "Only trigger nodes from their exact center.", false)
  private val zeroTickBreak = CheckboxSetting("0t Break", "Break every configured block in a break node on the same tick.", false)
  private val useLegacyEyeHeight = CheckboxSetting("Use 1.8 Height", "Use RSA's 1.54 eye-height approximation for node rotations.", true)

  private val triggerBind = KeyBindSetting("Trigger Bind", "Consumes click/secrets awaits for the current node.", KeyBind(-1))
  private val reloadBind = KeyBindSetting("Reload Routes", "Reload config/phantom/autoroutes/routes.json.", KeyBind(-1))

  private val triggerRadius = SliderSetting("Trigger Radius", "Default node trigger radius.", 0.5, 0.2, 3.0, step = 0.1)
  private val lineWidth = SliderSetting("Line Width", "Node link line thickness.", 2.0, 1.0, 6.0, step = 0.5)

  private val startColor = ColorSetting("Start Color", "Start node color.", 0xFF4ADE80.toInt())
  private val etherwarpColor = ColorSetting("Etherwarp Color", "Etherwarp node color.", 0xFF22D3EE.toInt())
  private val breakColor = ColorSetting("Break Color", "Break node color.", 0xFFFACC15.toInt())
  private val boomColor = ColorSetting("Boom Color", "Boom node color.", 0xFFFF5C7A.toInt())
  private val batColor = ColorSetting("Bat Color", "Bat node color.", 0xFF60A5FA.toInt())
  private val aotvColor = ColorSetting("AOTV Color", "AOTV node color.", 0xFFC084FC.toInt())
  private val useColor = ColorSetting("Use Color", "Use node color.", 0xFFFFFFFF.toInt())

  private var savedNodes: Map<String, List<AutoRouteNode>> = emptyMap()
  private var activeSignature: String? = null
  private var activeNodes: List<AutoRouteNode> = emptyList()
  private var inNode: AutoRouteNode? = null
  private var tickTime = 0
  private var lastUseItemOnTicks = 0
  private var forceSneakTicks = 0
  private var loadedOnce = false

  private val secretNames = setOf(
    "Health Potion VIII Splash Potion",
    "Healing Potion 8 Splash Potion",
    "Healing Potion VIII Splash Potion",
    "Healing VIII Splash Potion",
    "Healing 8 Splash Potion",
    "Decoy",
    "Inflatable Jerry",
    "Spirit Leap",
    "Trap",
    "Training Weights",
    "Defuse Kit",
    "Dungeon Chest Key",
    "Treasure Talisman",
    "Revive Stone",
    "Architect's First Draft",
    "Secret Dye",
    "Candycomb",
  )

  init {
    addSetting(
      enabled,
      executeNodes,
      renderNodes,
      chatMessages,
      centerOnly,
      zeroTickBreak,
      useLegacyEyeHeight,
      triggerBind,
      reloadBind,
      triggerRadius,
      lineWidth,
      startColor,
      etherwarpColor,
      breakColor,
      boomColor,
      batColor,
      aotvColor,
      useColor,
    )
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(@Suppress("UNUSED_PARAMETER") event: TickEvent.Start) {
    if (!enabled.value) return
    DungeonScanState.tick()
    tickTime++
    if (lastUseItemOnTicks > 0) lastUseItemOnTicks--
    if (forceSneakTicks > 0 && --forceSneakTicks <= 0) {
      mc.options.keyShift?.setDown(false)
    }

    if (!loadedOnce) reloadRoutes(sendChat = false)
    if (reloadBind.value.isPressed()) reloadRoutes(sendChat = true)
    if (triggerBind.value.isPressed()) consumeTriggerAwaits()

    val player = mc.player ?: return resetActiveRoom()
    val room = DungeonScanState.currentRoom ?: return resetActiveRoom()
    if (!DungeonScanState.isInDungeon || room.corner == null || room.direction == null) return resetActiveRoom()

    val signature = roomSignature(room)
    if (signature != activeSignature) {
      activeSignature = signature
      activeNodes = findRoomNodes(room).onEach { it.calculate(room) }
      inNode = null
      if (chatMessages.value && activeNodes.isNotEmpty()) {
        ChatUtils.sendMessage("Auto Routes: loaded ${activeNodes.size} nodes for ${room.name}.")
      }
    }

    if (!executeNodes.value || activeNodes.isEmpty() || mc.screen != null) return

    val playerPos = MutableVec(player.x, player.y, player.z)
    activeNodes.forEach { it.updateNodeState(playerPos, tickTime, centerOnly.value) }
    var lastType: NodeKind? = null
    while (handleQueue(playerPos, lastType).also { if (it != null) lastType = it } != null) {
    }
  }

  @SubscribeEvent
  fun onRender(event: WorldRenderEvent.Last) {
    if (!enabled.value || !renderNodes.value) return
    val nodes = activeNodes
    if (nodes.isEmpty()) return

    for (node in nodes) {
      val color = nodeColor(node)
      Render3D.drawStyledBox(
        event.context,
        nodeBox(node.realPos ?: continue, node.radius),
        color,
        Color(color.red, color.green, color.blue, 55),
        true,
        2.0f,
      )
      Render3D.drawWorldLabel(event.context, node.realPos!!.toVec3().add(0.0, 0.65, 0.0), node.kind.id, color, 0.75f)

      when (node) {
        is AutoRouteNode.Etherwarp -> {
          val target = node.realTarget ?: continue
          Render3D.drawLine(event.context, node.realPos!!.toVec3(), target.toVec3(), color, true, lineWidth.value.toFloat())
          Render3D.drawStyledBox(event.context, blockBox(target.blockPos()), color, Color(color.red, color.green, color.blue, 45), true, 1.5f)
        }
        is AutoRouteNode.Boom -> {
          val target = node.realTarget ?: continue
          Render3D.drawLine(event.context, node.realPos!!.toVec3(), target.toVec3(), color, true, lineWidth.value.toFloat())
          Render3D.drawStyledBox(event.context, blockBox(target.blockPos()), color, Color(color.red, color.green, color.blue, 45), true, 1.5f)
        }
        is AutoRouteNode.Break -> {
          for (block in node.realBlocks) {
            Render3D.drawStyledBox(event.context, blockBox(block.blockPos()), color, Color(color.red, color.green, color.blue, 45), true, 1.4f)
          }
        }
        else -> Unit
      }
    }
  }

  @SubscribeEvent
  fun onOutgoingPacket(event: PacketEvent.Outgoing) {
    if (!enabled.value) return
    val packet = event.packet as? ServerboundUseItemOnPacket ?: return
    val node = inNode ?: return
    if (!node.awaitManager.has(AwaitKind.SECRETS)) return
    val level = mc.level ?: return
    val hit = packet.hitResult
    val block = level.getBlockState(hit.blockPos).block
    if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.PLAYER_HEAD || block == Blocks.LEVER) {
      node.awaitManager.consumeSecrets(1)
      lastUseItemOnTicks = 2
    }
  }

  @SubscribeEvent
  fun onIncomingPacket(event: PacketEvent.Incoming) {
    if (!enabled.value || inNode?.awaitManager?.has(AwaitKind.SECRETS) != true) return
    val level = mc.level ?: return
    when (val packet = event.packet) {
      is ClientboundTakeItemEntityPacket -> {
        val item = level.getEntity(packet.itemId) as? ItemEntity ?: return
        if (item.item.hoverName.string.stripFormatting() in secretNames) inNode?.awaitManager?.consumeSecrets(1)
      }
      is ClientboundRemoveEntitiesPacket -> {
        for (id in packet.entityIds) {
          val item = level.getEntity(id) as? ItemEntity ?: continue
          if (item.distanceToSqr(mc.player ?: return) < 64.0 && item.item.hoverName.string.stripFormatting() in secretNames) {
            inNode?.awaitManager?.consumeSecrets(1)
          }
        }
      }
    }
  }

  private fun handleQueue(playerPos: MutableVec, lastType: NodeKind?): NodeKind? {
    val eligible = activeNodes
      .filter { it.isInNode(playerPos, centerOnly.value) && !it.triggered && !it.hasRanThisTick(tickTime) }
      .sortedByDescending { it.priority }

    if (eligible.isEmpty()) {
      inNode = null
      return null
    }

    val node = eligible.first()
    if (inNode !== node) {
      inNode = node
      node.awaitManager.onEnter()
    }
    if (node.shouldAwait() || lastUseItemOnTicks > 0 || (lastType != null && lastType != node.kind)) return null

    node.preTrigger(tickTime)
    return if (runNode(node, playerPos)) node.kind else null
  }

  private fun runNode(node: AutoRouteNode, playerPos: MutableVec): Boolean {
    val player = mc.player ?: return node.cancel()
    if (chatMessages.value) ChatUtils.sendMessage("Auto Routes: ${node.kind.id}")

    return when (node) {
      is AutoRouteNode.Etherwarp -> {
        val target = node.realTarget ?: return node.cancel()
        if (!selectHotbar("Aspect of the Void", "Aspect of the End")) return node.cancel()
        pressSneak(4)
        rotateTo(target.toVec3())
        tapUse()
        playerPos.set(target.x, target.y + 1.05, target.z)
        true
      }
      is AutoRouteNode.Aotv -> {
        if (!selectHotbar("Aspect of the Void", "Aspect of the End")) return node.cancel()
        releaseSneakSoon()
        rotateDirection(node.realRotation ?: return node.cancel())
        tapUse()
        true
      }
      is AutoRouteNode.Use -> {
        if (!selectHotbar(node.itemId)) return node.cancel()
        if (node.sneak) pressSneak(4) else releaseSneakSoon()
        rotateDirection(node.realRotation ?: return node.cancel())
        tapUse()
        true
      }
      is AutoRouteNode.Boom -> {
        val target = node.realTarget ?: return node.cancel()
        if (!selectHotbar("Superboom", "TNT")) return node.cancel()
        rotateTo(target.toVec3())
        tapUse()
        false
      }
      is AutoRouteNode.Bat -> {
        val nearbyBat = nearestBat(player.position()) ?: return node.cancel()
        if (!selectHotbar("Hyperion", "Astraea", "Scylla", "Valkyrie", "Allium")) return node.cancel()
        rotateTo(nearbyBat.eyePosition)
        tapUse()
        false
      }
      is AutoRouteNode.Break -> {
        val blocks = node.realBlocks.filter { player.distanceToSqr(it.toVec3()) <= 25.0 }
        if (blocks.isEmpty()) return true
        if (!selectHotbar("Stonk", "Pickaxe", "Drill")) return node.cancel()
        if (zeroTickBreak.value) {
          blocks.forEach { breakBlock(it.blockPos()) }
        } else {
          blocks.forEachIndexed { index, pos -> TickScheduler.schedule(index.toLong()) { breakBlock(pos.blockPos()) } }
        }
        node.cancel()
      }
    }
  }

  private fun reloadRoutes(sendChat: Boolean) {
    loadedOnce = true
    routesFile.parentFile?.mkdirs()
    if (!routesFile.exists()) {
      savedNodes = emptyMap()
      if (sendChat || chatMessages.value) {
        ChatUtils.sendMessage("Auto Routes: put RSA routes at config/phantom/autoroutes/routes.json")
      }
      return
    }

    savedNodes = runCatching {
      val root = JsonParser.parseString(routesFile.readText()).asJsonObject
      buildMap {
        for ((roomName, value) in root.entrySet()) {
          if (!value.isJsonArray) continue
          val nodes = value.asJsonArray.mapNotNull { parseNode(it.asJsonObject) }
          if (nodes.isNotEmpty()) put(normalizeKey(roomName), nodes)
        }
      }
    }.getOrElse { error ->
      if (sendChat || chatMessages.value) ChatUtils.sendMessage("Auto Routes: failed to load routes.json (${error.message}).")
      emptyMap()
    }

    activeSignature = null
    if (sendChat || chatMessages.value) ChatUtils.sendMessage("Auto Routes: loaded ${savedNodes.values.sumOf { it.size }} nodes.")
  }

  private fun parseNode(obj: JsonObject): AutoRouteNode? {
    val kind = NodeKind.from(obj["type"]?.asString) ?: return null
    val localPos = parseVec(obj["localPos"]) ?: return null
    val radius = obj["radius"]?.asDouble ?: triggerRadius.value
    val start = obj["start"]?.asBoolean ?: false
    val awaits = AwaitManager.fromJson(obj.getAsJsonObject("awaits"))
    return when (kind) {
      NodeKind.ETHERWARP -> AutoRouteNode.Etherwarp(localPos, parseVec(obj["localTarget"]) ?: return null, awaits, radius, start)
      NodeKind.BOOM -> AutoRouteNode.Boom(localPos, parseVec(obj["target"]) ?: return null, awaits, radius, start)
      NodeKind.BAT -> AutoRouteNode.Bat(localPos, obj["yaw"]?.asFloat ?: 0f, obj["pitch"]?.asFloat ?: 90f, awaits, radius, start)
      NodeKind.AOTV -> AutoRouteNode.Aotv(localPos, parseVec(obj["rotationVec"]) ?: return null, awaits, radius, start)
      NodeKind.BREAK -> {
        val blocks = obj.getAsJsonArray("blocks")?.mapNotNull { parseVec(it) }.orEmpty()
        AutoRouteNode.Break(localPos, blocks, awaits, radius, start)
      }
      NodeKind.USE -> AutoRouteNode.Use(
        localPos,
        parseVec(obj["rotationVec"]) ?: return null,
        obj["itemID"]?.asString.orEmpty(),
        obj["sneak"]?.asBoolean ?: false,
        awaits,
        radius,
        start,
      )
    }
  }

  private fun findRoomNodes(room: DungeonRoom): List<AutoRouteNode> {
    val names = buildList {
      room.name?.let {
        add(it)
        if (room.totalSecrets > 0) add("$it-${room.totalSecrets}")
      }
    }
    for (name in names) {
      savedNodes[normalizeKey(name)]?.let { return it.map { node -> node.copyFresh() } }
    }
    return emptyList()
  }

  private fun consumeTriggerAwaits() {
    val node = inNode ?: return
    node.awaitManager.consumeClick()
    node.awaitManager.consumeSecrets(100)
    if (node is AutoRouteNode.Bat) node.triggered = true
  }

  private fun resetActiveRoom() {
    activeSignature = null
    activeNodes = emptyList()
    inNode = null
  }

  private fun roomSignature(room: DungeonRoom): String =
    "${room.name}:${room.totalSecrets}:${room.corner}:${room.rotation}"

  private fun transform(room: DungeonRoom, pos: MutableVec): MutableVec? {
    val corner = room.corner ?: return null
    return when (room.direction ?: return null) {
      RoomDirection.NW -> MutableVec(pos.x + corner.x, pos.y, pos.z + corner.z)
      RoomDirection.NE -> MutableVec(-pos.z + corner.x, pos.y, pos.x + corner.z)
      RoomDirection.SW -> MutableVec(pos.z + corner.x, pos.y, -pos.x + corner.z)
      RoomDirection.SE -> MutableVec(-pos.x + corner.x, pos.y, -pos.z + corner.z)
    }
  }

  private fun rotateDirection(room: DungeonRoom, vec: MutableVec): MutableVec? =
    when (room.direction ?: return null) {
      RoomDirection.NW -> vec.copy()
      RoomDirection.NE -> MutableVec(-vec.z, vec.y, vec.x)
      RoomDirection.SW -> MutableVec(vec.z, vec.y, -vec.x)
      RoomDirection.SE -> MutableVec(-vec.x, vec.y, -vec.z)
    }

  private fun rotateTo(target: Vec3) {
    val player = mc.player ?: return
    val eye = if (useLegacyEyeHeight.value) player.position().add(0.0, 1.54, 0.0) else player.eyePosition
    val rotation = AngleUtils.getRotation(eye, target)
    player.yRot = rotation.yaw
    player.xRot = rotation.pitch
  }

  private fun rotateDirection(direction: MutableVec) {
    val player = mc.player ?: return
    val eye = player.position().add(0.0, if (useLegacyEyeHeight.value) 1.54 else player.eyeHeight.toDouble(), 0.0)
    rotateTo(eye.add(direction.x, direction.y, direction.z))
  }

  private fun tapUse() {
    val player = mc.player ?: return
    mc.gameMode?.useItem(player, InteractionHand.MAIN_HAND)
    player.swing(InteractionHand.MAIN_HAND)
    mc.options.keyUse?.setDown(true)
    TickScheduler.schedule(1) { mc.options.keyUse?.setDown(false) }
  }

  private fun breakBlock(pos: BlockPos) {
    val player = mc.player ?: return
    mc.gameMode?.startDestroyBlock(pos, Direction.UP)
    player.swing(InteractionHand.MAIN_HAND)
  }

  private fun selectHotbar(vararg queries: String): Boolean {
    val player = mc.player ?: return false
    val normalized = queries.map { it.normalizedItemQuery() }.filter { it.isNotBlank() }
    if (normalized.isEmpty()) return true
    for (slot in 0..8) {
      val stack = player.inventory.getItem(slot)
      if (stack.isEmpty) continue
      val haystack = stack.searchText()
      if (normalized.any { it in haystack }) {
        player.inventory.selectedSlot = slot
        return true
      }
    }
    return false
  }

  private fun nearestBat(pos: Vec3): Entity? {
    val level = mc.level ?: return null
    val box = AABB(pos, pos).inflate(10.0)
    return level.getEntitiesOfClass(Bat::class.java, box).minByOrNull { it.distanceToSqr(pos) }
  }

  private fun pressSneak(ticks: Int) {
    mc.options.keyShift?.setDown(true)
    forceSneakTicks = maxOf(forceSneakTicks, ticks)
  }

  private fun releaseSneakSoon() {
    forceSneakTicks = 1
  }

  private fun nodeColor(node: AutoRouteNode): Color {
    val argb = if (node.start) {
      startColor.value
    } else {
      when (node.kind) {
        NodeKind.ETHERWARP -> etherwarpColor.value
        NodeKind.BREAK -> breakColor.value
        NodeKind.BOOM -> boomColor.value
        NodeKind.BAT -> batColor.value
        NodeKind.AOTV -> aotvColor.value
        NodeKind.USE -> useColor.value
      }
    }
    return Color(argb, true)
  }

  private fun parseVec(element: JsonElement?): MutableVec? {
    if (element == null || element.isJsonNull) return null
    if (element.isJsonArray) {
      val arr = element.asJsonArray
      if (arr.size() < 3) return null
      return MutableVec(arr[0].asDouble, arr[1].asDouble, arr[2].asDouble)
    }
    if (!element.isJsonObject) return null
    val obj = element.asJsonObject
    return MutableVec(
      obj["x"]?.asDouble ?: obj["field_1352"]?.asDouble ?: return null,
      obj["y"]?.asDouble ?: obj["field_1351"]?.asDouble ?: return null,
      obj["z"]?.asDouble ?: obj["field_1350"]?.asDouble ?: return null,
    )
  }

  private fun normalizeKey(value: String): String =
    value.lowercase().filter { it.isLetterOrDigit() }

  private fun nodeBox(pos: MutableVec, radius: Double): AABB =
    AABB(pos.x - radius, pos.y - 0.04, pos.z - radius, pos.x + radius, pos.y + 0.08, pos.z + radius)

  private fun blockBox(pos: BlockPos): AABB =
    AABB(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), pos.x + 1.0, pos.y + 1.0, pos.z + 1.0)

  private fun String.stripFormatting(): String = replace(Regex("§."), "")

  private fun String.normalizedItemQuery(): String =
    lowercase().replace("_", " ").replace(Regex("[^a-z0-9 ]"), "").trim()

  private fun ItemStack.searchText(): String =
    (hoverName.string + " " + item.toString()).normalizedItemQuery()

  private enum class NodeKind(val id: String, val priority: Int) {
    ETHERWARP("etherwarp", 5),
    AOTV("aotv", 8),
    USE("use", 8),
    BAT("bat", 16),
    BREAK("break", 18),
    BOOM("boom", 20);

    companion object {
      fun from(value: String?): NodeKind? =
        entries.firstOrNull { it.id.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) || (it == ETHERWARP && value.equals("ew", true)) }
    }
  }

  private enum class AwaitKind(val id: String) {
    CLICK("click"),
    SECRETS("secrets"),
    ETHERWARP_TRACE("etherwarptrace");

    companion object {
      fun from(value: String): AwaitKind? =
        entries.firstOrNull { it.id.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) }
    }
  }

  private class AwaitManager(private val awaits: Map<AwaitKind, Int>) {
    private var clicked = false
    private var collectedSecrets = 0

    fun has(kind: AwaitKind): Boolean = kind in awaits
    fun hasAny(): Boolean = awaits.isNotEmpty()
    fun onEnter() {
      clicked = false
      collectedSecrets = 0
    }
    fun shouldAwait(): Boolean =
      awaits.any { (kind, amount) ->
        when (kind) {
          AwaitKind.CLICK -> !clicked
          AwaitKind.SECRETS -> collectedSecrets < amount
          AwaitKind.ETHERWARP_TRACE -> false
        }
      }
    fun consumeClick() {
      clicked = true
    }
    fun consumeSecrets(amount: Int) {
      collectedSecrets += amount
    }
    fun copyFresh(): AwaitManager = AwaitManager(awaits)

    companion object {
      fun fromJson(obj: JsonObject?): AwaitManager {
        if (obj == null) return AwaitManager(emptyMap())
        val map = buildMap {
          for ((key, value) in obj.entrySet()) {
            val kind = AwaitKind.from(key) ?: continue
            put(kind, if (kind == AwaitKind.SECRETS) value.asInt else 1)
          }
        }
        return AwaitManager(map)
      }
    }
  }

  private sealed class AutoRouteNode(
    val kind: NodeKind,
    val localPos: MutableVec,
    val awaitManager: AwaitManager,
    val radius: Double,
    val start: Boolean,
  ) {
    var realPos: MutableVec? = null
    var triggered = false
    private var lastTickTime = -1
    val priority: Int get() = kind.priority

    open fun calculate(room: DungeonRoom) {
      realPos = transform(room, localPos)
      awaitManager.onEnter()
    }

    fun hasRanThisTick(tickTime: Int): Boolean = tickTime <= lastTickTime
    fun preTrigger(tickTime: Int) {
      lastTickTime = tickTime
      triggered = true
    }
    fun shouldAwait(): Boolean = awaitManager.hasAny() && awaitManager.shouldAwait()
    fun cancel(): Boolean {
      triggered = false
      return false
    }
    fun isInNode(playerPos: MutableVec, centerOnly: Boolean): Boolean {
      val pos = realPos ?: return false
      if (centerOnly) {
        return pos.x == playerPos.x && playerPos.y in (pos.y - 0.05)..(pos.y + 0.05) && pos.z == playerPos.z
      }
      return playerPos.distanceToSqr(pos) <= radius * radius
    }
    fun updateNodeState(playerPos: MutableVec, tickTime: Int, centerOnly: Boolean) {
      if (tickTime <= lastTickTime) return
      if (!isInNode(playerPos, centerOnly) && triggered) triggered = false
    }

    abstract fun copyFresh(): AutoRouteNode

    class Etherwarp(localPos: MutableVec, val localTarget: MutableVec, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.ETHERWARP, localPos, awaits, radius, start) {
      var realTarget: MutableVec? = null
      override fun calculate(room: DungeonRoom) {
        super.calculate(room)
        realTarget = transform(room, localTarget)
      }
      override fun copyFresh() = Etherwarp(localPos.copy(), localTarget.copy(), awaitManager.copyFresh(), radius, start)
    }

    class Boom(localPos: MutableVec, val localTarget: MutableVec, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.BOOM, localPos, awaits, radius, start) {
      var realTarget: MutableVec? = null
      override fun calculate(room: DungeonRoom) {
        super.calculate(room)
        realTarget = transform(room, localTarget)
      }
      override fun copyFresh() = Boom(localPos.copy(), localTarget.copy(), awaitManager.copyFresh(), radius, start)
    }

    class Bat(localPos: MutableVec, val yaw: Float, val pitch: Float, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.BAT, localPos, awaits, radius, start) {
      override fun copyFresh() = Bat(localPos.copy(), yaw, pitch, awaitManager.copyFresh(), radius, start)
    }

    class Aotv(localPos: MutableVec, val localRotation: MutableVec, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.AOTV, localPos, awaits, radius, start) {
      var realRotation: MutableVec? = null
      override fun calculate(room: DungeonRoom) {
        super.calculate(room)
        realRotation = rotateDirection(room, localRotation)
      }
      override fun copyFresh() = Aotv(localPos.copy(), localRotation.copy(), awaitManager.copyFresh(), radius, start)
    }

    class Break(localPos: MutableVec, val blocks: List<MutableVec>, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.BREAK, localPos, awaits, radius, start) {
      var realBlocks: List<MutableVec> = emptyList()
      override fun calculate(room: DungeonRoom) {
        super.calculate(room)
        realBlocks = blocks.mapNotNull { transform(room, it) }
      }
      override fun copyFresh() = Break(localPos.copy(), blocks.map { it.copy() }, awaitManager.copyFresh(), radius, start)
    }

    class Use(localPos: MutableVec, val localRotation: MutableVec, val itemId: String, val sneak: Boolean, awaits: AwaitManager, radius: Double, start: Boolean) :
      AutoRouteNode(NodeKind.USE, localPos, awaits, radius, start) {
      var realRotation: MutableVec? = null
      override fun calculate(room: DungeonRoom) {
        super.calculate(room)
        realRotation = rotateDirection(room, localRotation)
      }
      override fun copyFresh() = Use(localPos.copy(), localRotation.copy(), itemId, sneak, awaitManager.copyFresh(), radius, start)
    }
  }

  private data class MutableVec(var x: Double, var y: Double, var z: Double) {
    fun copy(): MutableVec = MutableVec(x, y, z)
    fun set(nx: Double, ny: Double, nz: Double) {
      x = nx
      y = ny
      z = nz
    }
    fun toVec3(): Vec3 = Vec3(x, y, z)
    fun blockPos(): BlockPos = BlockPos.containing(x, y, z)
    fun distanceToSqr(other: MutableVec): Double {
      val dx = x - other.x
      val dy = y - other.y
      val dz = z - other.z
      return dx * dx + dy * dy + dz * dz
    }
  }
}
