package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class BackgroundSystem(
    private val batch: SpriteBatch,
    private val assets: Assets,
    private val camera: OrthographicCamera
) : Disposable {

    private val nebulaRenderer = NebulaRenderer(assets)
    private val starsRenderer = StarsRenderer()
    private val debrisRenderer = DebrisRenderer()
    private val lightOverlayRenderer = LightOverlayRenderer()

    // Cumulative scroll tracking to avoid jumps when camera wraps
    private var lastCamX = 0f
    private var lastCamY = 0f
    private var cumulativeScrollX = 0f
    private var cumulativeScrollY = 0f
    private var initialized = false

    fun update(delta: Float) {
        val camX = camera.position.x
        val camY = camera.position.y

        if (!initialized) {
            lastCamX = camX
            lastCamY = camY
            initialized = true
        }

        // Calculate delta movement, handling wrap-around
        var dx = camX - lastCamX
        var dy = camY - lastCamY

        // Detect wrap-around (sudden large jump)
        val wrapThreshold = Constants.WORLD_WIDTH / 2
        if (dx > wrapThreshold) dx -= Constants.WORLD_WIDTH
        if (dx < -wrapThreshold) dx += Constants.WORLD_WIDTH
        if (dy > Constants.WORLD_HEIGHT / 2) dy -= Constants.WORLD_HEIGHT
        if (dy < -Constants.WORLD_HEIGHT / 2) dy += Constants.WORLD_HEIGHT

        cumulativeScrollX += dx
        cumulativeScrollY += dy

        lastCamX = camX
        lastCamY = camY

        nebulaRenderer.update(delta, cumulativeScrollX, cumulativeScrollY)
        starsRenderer.update(delta, camera)
        debrisRenderer.update(delta)
        lightOverlayRenderer.update()
    }

    fun renderBackground() {
        // 1. Stars (Parallax) - Furthest back, drawn first
        starsRenderer.render(batch, camera, cumulativeScrollX, cumulativeScrollY)

        // 2. Nebula (Shader) - Drawn on top with alpha, partially obscures stars
        nebulaRenderer.render(batch, camera)
    }

    fun renderForeground() {
        // 3. Debris (Fast foreground)
        debrisRenderer.render(batch, camera)

        // 4. Light Overlays (Halos/Cookies) - Additive blending
        lightOverlayRenderer.render(batch, camera)
    }

    override fun dispose() {
        nebulaRenderer.dispose()
        starsRenderer.dispose()
        debrisRenderer.dispose()
        lightOverlayRenderer.dispose()
    }
}
