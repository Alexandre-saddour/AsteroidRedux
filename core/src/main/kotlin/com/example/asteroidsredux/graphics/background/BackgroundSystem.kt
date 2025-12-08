package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

/**
 * Production-ready background rendering system with full runtime control.
 */
class BackgroundSystem(
    private val batch: SpriteBatch,
    private val assets: Assets,
    private val camera: OrthographicCamera
) : Disposable {

    private val nebulaRenderer = NebulaRenderer(assets)
    private val starsRenderer = StarsRenderer(assets)
    private val debrisRenderer = DebrisRenderer(assets)
    private val lightOverlayRenderer = LightOverlayRenderer(assets)

    // === Animation time tracking ===
    private var time = 0f

    // === Cumulative scroll tracking ===
    private var lastCamX = 0f
    private var lastCamY = 0f
    private var cumulativeScrollX = 0f
    private var cumulativeScrollY = 0f
    private var initialized = false

    // === Master rendering parameters ===
    data class RenderParams(
        // Layer controls
        var enableStars: Boolean = true,
        var enableNebula: Boolean = true,
        var enableDebris: Boolean = true,
        var enableLightOverlays: Boolean = true,

        // Visual quality
        var starBrightness: Float = 1f,
        var nebulaIntensity: Float = 0.6f,
        var debrisAlpha: Float = 0.5f,

        // Color grading
        var globalTintR: Float = 1f,
        var globalTintG: Float = 1f,
        var globalTintB: Float = 1f,

        // Adaptive effects
        var adaptiveBrightnessTarget: Float = 1f,
        var adaptiveBrightnessSpeed: Float = 2f
    )

    val params = RenderParams()

    // === Debug statistics ===
    data class DebugStats(
        var drawCalls: Int = 0,
        var starDraws: Int = 0,
        var debrisDraws: Int = 0,
        var overlayDraws: Int = 0,
        var frameTimeMs: Float = 0f
    )

    val debugStats = DebugStats()

    // Adaptive brightness (smoothly transitions)
    private var currentAdaptiveBrightness = 1f

    // Direct access to renderer params for fine-tuning
    val starParams get() = starsRenderer.params
    val nebulaParams get() = nebulaRenderer.params
    val debrisParams get() = debrisRenderer.params
    val lightParams get() = lightOverlayRenderer.params

    fun update(delta: Float) {
        time += delta

        val camX = camera.position.x
        val camY = camera.position.y

        if (!initialized) {
            lastCamX = camX
            lastCamY = camY
            initialized = true
        }

        // Calculate delta movement with wrap detection
        var dx = camX - lastCamX
        var dy = camY - lastCamY

        val wrapThresholdX = Constants.WORLD_WIDTH / 2
        val wrapThresholdY = Constants.WORLD_HEIGHT / 2
        if (dx > wrapThresholdX) dx -= Constants.WORLD_WIDTH
        if (dx < -wrapThresholdX) dx += Constants.WORLD_WIDTH
        if (dy > wrapThresholdY) dy -= Constants.WORLD_HEIGHT
        if (dy < -wrapThresholdY) dy += Constants.WORLD_HEIGHT

        cumulativeScrollX += dx
        cumulativeScrollY += dy
        lastCamX = camX
        lastCamY = camY

        // Smooth adaptive brightness transition
        val brightDiff = params.adaptiveBrightnessTarget - currentAdaptiveBrightness
        currentAdaptiveBrightness += brightDiff * params.adaptiveBrightnessSpeed * delta

        // Apply global tint to renderers
        starsRenderer.setGlobalTint(params.globalTintR, params.globalTintG, params.globalTintB)
        nebulaRenderer.setTint(params.globalTintR, params.globalTintG, params.globalTintB)
        lightOverlayRenderer.setGlobalLightColor(params.globalTintR, params.globalTintG, params.globalTintB)

        // Update renderers
        nebulaRenderer.update(delta, cumulativeScrollX, cumulativeScrollY)
        nebulaRenderer.setIntensity(params.nebulaIntensity * currentAdaptiveBrightness)

        starsRenderer.update(delta, time)
        debrisRenderer.update(delta, time, cumulativeScrollX, cumulativeScrollY)
        lightOverlayRenderer.update(delta, time)
    }

    fun renderBackground() {
        val startTime = System.nanoTime()
        debugStats.drawCalls = 0

        // 1. Stars
        if (params.enableStars) {
            starsRenderer.render(batch, camera, cumulativeScrollX, cumulativeScrollY,
                params.starBrightness * currentAdaptiveBrightness)
            debugStats.starDraws = starsRenderer.lastDrawCount
            debugStats.drawCalls += starsRenderer.lastDrawCount
        }

        // 2. Nebula
        if (params.enableNebula) {
            nebulaRenderer.render(batch, camera)
            debugStats.drawCalls += 1
        }

        debugStats.frameTimeMs = (System.nanoTime() - startTime) / 1_000_000f
    }

    fun renderForeground() {
        // 3. Debris
        if (params.enableDebris) {
            debrisRenderer.render(batch, camera, params.debrisAlpha)
            debugStats.debrisDraws = debrisRenderer.lastDrawCount
            debugStats.drawCalls += debrisRenderer.lastDrawCount
        }

        // 4. Light Overlays
        if (params.enableLightOverlays) {
            lightOverlayRenderer.render(batch, camera)
            debugStats.overlayDraws = lightOverlayRenderer.lastDrawCount
            debugStats.drawCalls += lightOverlayRenderer.lastDrawCount
        }
    }

    /** Set global tint for color grading */
    fun setGlobalTint(r: Float, g: Float, b: Float) {
        params.globalTintR = r
        params.globalTintG = g
        params.globalTintB = b
    }

    /** Trigger adaptive brightness change (e.g., dim during explosions) */
    fun setAdaptiveBrightness(target: Float) {
        params.adaptiveBrightnessTarget = target.coerceIn(0.3f, 1.5f)
    }

    /** Add explosion halo at position */
    fun addExplosionHalo(x: Float, y: Float, size: Float = 100f) {
        lightOverlayRenderer.addHalo(x, y, size, Color(1f, 0.6f, 0.3f, 0.5f))
    }

    /** Get debug info string for overlay */
    fun getDebugString(): String {
        return "BG: ${debugStats.drawCalls} draws (★${debugStats.starDraws} ◆${debugStats.debrisDraws} ○${debugStats.overlayDraws}) ${String.format("%.1f", debugStats.frameTimeMs)}ms"
    }

    override fun dispose() {
        nebulaRenderer.dispose()
        starsRenderer.dispose()
        debrisRenderer.dispose()
        lightOverlayRenderer.dispose()
    }
}
