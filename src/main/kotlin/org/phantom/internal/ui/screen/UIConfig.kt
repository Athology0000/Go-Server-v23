package org.phantom.internal.ui.screen

import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import org.phantom.api.event.EventBus
import org.phantom.api.event.annotation.SubscribeEvent
import org.phantom.api.event.impl.render.NvgEvent
import org.phantom.api.util.ui.NVGRenderer
import org.phantom.internal.helper.Config
import org.phantom.internal.ui.UIScreen
import org.phantom.internal.ui.animation.BounceAnimation
import org.phantom.internal.ui.components.tooltips.TooltipManager
import org.phantom.internal.ui.panel.UIPanel
import org.phantom.internal.ui.panel.panels.UIAddonList
import org.phantom.internal.ui.panel.panels.UISidebar

internal object UIConfig : UIScreen() {

  private val openAnim = BounceAnimation(400)
  private var wasClosed = true

  private val sidebar = UISidebar()
  private var body: UIPanel = UIAddonList()
  private var auxPanel: UIPanel? = null

  init {
    EventBus.register(this)
  }

  @Suppress("unused")
  @SubscribeEvent
  fun onRender(event: NvgEvent) {
    if (mc.screen != this)
      return

    val window = mc.window
    val width = window.screenWidth.toFloat()
    val height = window.screenHeight.toFloat()

    NVGRenderer.beginFrame(width, height)

    if (openAnim.isAnimating()) {
      val scale = openAnim.get(0f, 1f)
      val cx = width / 2f
      val cy = height / 2f

      NVGRenderer.translate(cx, cy)
      NVGRenderer.scale(scale, scale)
      NVGRenderer.translate(-cx, -cy)
    }

    val aux = auxPanel
    val contentWidth =
      sidebar.width + BODY_GAP + body.width + if (aux != null) AUX_GAP + aux.width else 0f
    val contentHeight = maxOf(sidebar.height, body.height, aux?.height ?: 0f)
    val originX = ((width - contentWidth) / 2f).coerceAtLeast(SCREEN_PADDING)
    val originY = ((height - contentHeight) / 2f).coerceAtLeast(SCREEN_PADDING)

    sidebar
      .updateBounds(originX, originY)
      .render()

    body
      .updateBounds(originX + sidebar.width + BODY_GAP, originY)
      .render()

    aux
      ?.updateBounds(originX + sidebar.width + BODY_GAP + body.width + AUX_GAP, originY)
      ?.render()

    TooltipManager.renderAll()
    NVGRenderer.endFrame()
  }

  override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
    return auxPanel?.mouseClicked(click.button()) == true ||
      body.mouseClicked(click.button()) ||
      sidebar.mouseClicked(click.button()) ||
      super.mouseClicked(click, doubled)
  }

  override fun mouseReleased(click: MouseButtonEvent): Boolean {
    return auxPanel?.mouseReleased(click.button()) == true ||
      body.mouseReleased(click.button()) ||
      super.mouseReleased(click)
  }

  override fun mouseDragged(click: MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean {
    return auxPanel?.mouseDragged(click.button(), offsetX, offsetY) == true ||
      body.mouseDragged(click.button(), offsetX, offsetY) ||
      super.mouseDragged(click, offsetX, offsetY)
  }

  override fun charTyped(input: CharacterEvent): Boolean {
    return body.charTyped(input) ||
      super.charTyped(input)
  }

  override fun keyPressed(input: KeyEvent): Boolean {
    return body.keyPressed(input) ||
      super.keyPressed(input)
  }

  override fun mouseScrolled(
    mouseX: Double,
    mouseY: Double,
    horizontalAmount: Double,
    verticalAmount: Double,
  ): Boolean {
    return auxPanel?.mouseScrolled(horizontalAmount, verticalAmount) == true ||
      body.mouseScrolled(horizontalAmount, verticalAmount) ||
      super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
  }

  override fun init() {
    if (wasClosed) {
      openAnim.start()
      wasClosed = false
    }

    super.init()
  }

  override fun onClose() {
    Config.saveModulesConfig()
    wasClosed = true
    super.onClose()
  }

  fun swapBodyPanel(panel: UIPanel): UIPanel {
    val previous = body
    this.body = panel
    auxPanel = null
    return previous
  }

  fun setAuxPanel(panel: UIPanel) { auxPanel = panel }
  fun clearAuxPanel() { auxPanel = null }

  fun getBodyPanel(): UIPanel {
    return body
  }

  private const val SCREEN_PADDING = 20f
  private const val BODY_GAP = 10f
  private const val AUX_GAP = 10f

}
