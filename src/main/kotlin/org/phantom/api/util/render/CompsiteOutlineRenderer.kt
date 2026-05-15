package org.phantom.api.util.render

import org.joml.Vector4f

/**
 * Compatibility shim for the experimental composite-outline renderer.
 *
 * Your Phantom project is using Mojang mappings, while the original renderer
 * skeleton used Yarn names such as MinecraftClient, ShaderProgram, RenderTarget,
 * BufferRenderer, etc. Those classes do not exist under those names in this
 * project, so keeping that file breaks compileKotlin.
 *
 * Mining block highlighting should use OverlayRenderEngine / Render3D for now.
 * This shim keeps any existing references compiling without trying to create
 * framebuffer/shader objects with the wrong mapping names.
 */
object CompsiteOutlineRenderer {
  var outlineThickness = 1.0f
  var glowRadius = 4.0f
  var glowStrength = 0.75f
  var outlineColor = Vector4f(0.0f, 0.7f, 1.0f, 1.0f)
  var glowColor = Vector4f(0.0f, 0.35f, 1.0f, 0.6f)

  fun init(shaderProgram: Any? = null) {
    // No-op until the renderer is ported to Mojang mappings.
  }

  fun resize() {
    // No-op until the renderer is ported to Mojang mappings.
  }

  fun beginMaskPass() {
    // No-op until the renderer is ported to Mojang mappings.
  }

  fun endMaskPass() {
    // No-op until the renderer is ported to Mojang mappings.
  }

  fun composite() {
    // No-op until the renderer is ported to Mojang mappings.
  }
}

/** Correctly-spelled alias for future code. */
object CompositeOutlineRenderer {
  var outlineThickness: Float
    get() = CompsiteOutlineRenderer.outlineThickness
    set(value) { CompsiteOutlineRenderer.outlineThickness = value }

  var glowRadius: Float
    get() = CompsiteOutlineRenderer.glowRadius
    set(value) { CompsiteOutlineRenderer.glowRadius = value }

  var glowStrength: Float
    get() = CompsiteOutlineRenderer.glowStrength
    set(value) { CompsiteOutlineRenderer.glowStrength = value }

  var outlineColor: Vector4f
    get() = CompsiteOutlineRenderer.outlineColor
    set(value) { CompsiteOutlineRenderer.outlineColor = value }

  var glowColor: Vector4f
    get() = CompsiteOutlineRenderer.glowColor
    set(value) { CompsiteOutlineRenderer.glowColor = value }

  fun init(shaderProgram: Any? = null) = CompsiteOutlineRenderer.init(shaderProgram)
  fun resize() = CompsiteOutlineRenderer.resize()
  fun beginMaskPass() = CompsiteOutlineRenderer.beginMaskPass()
  fun endMaskPass() = CompsiteOutlineRenderer.endMaskPass()
  fun composite() = CompsiteOutlineRenderer.composite()
}
