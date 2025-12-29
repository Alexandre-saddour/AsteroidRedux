package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants
import kotlin.math.sin

/**
 * Procedural nebula renderer with runtime color grading and visual tuning.
 */
class NebulaRenderer(assets: Assets) : Disposable {
    private val shader: ShaderProgram
    private val emptyTexture: Texture

    /** Runtime tuning parameters */
    data class NebulaParams(
        var tintR: Float = 1f,
        var tintG: Float = 1f,
        var tintB: Float = 1f,
        var intensity: Float = 0.85f,     // Increased for better visibility
        var noiseScale: Float = 1f,      // Noise frequency multiplier
        var scrollSpeed: Float = 1f,     // Scroll speed multiplier
        var contrast: Float = 1f,        // Contrast enhancement
        var lightDirX: Float = 0.3f,     // Pseudo-light direction X
        var lightDirY: Float = 0.5f,     // Pseudo-light direction Y
        var colorAnimSpeed: Float = 0f   // Speed of tint animation (0 = disabled)
    )

    val params = NebulaParams()

    init {
        val vert = Gdx.files.internal("shaders/default.vert")
        val frag = Gdx.files.internal("shaders/nebula.frag")
        shader = ShaderProgram(vert, frag)
        if (!shader.isCompiled) {
            Gdx.app.error("NebulaRenderer", "Shader compilation failed:\n" + shader.log)
        }

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        emptyTexture = Texture(pixmap)
        pixmap.dispose()
    }

    private var time = 0f
    private var scrollX = 0f
    private var scrollY = 0f

    /** Set nebula color tint (RGB, 0–1 range) */
    fun setTint(r: Float, g: Float, b: Float) {
        params.tintR = r
        params.tintG = g
        params.tintB = b
    }

    /** Set nebula intensity/opacity (0–1 range) */
    fun setIntensity(value: Float) {
        params.intensity = value.coerceIn(0f, 1f)
    }

    /** Set pseudo-light direction for fake depth */
    fun setLightDirection(x: Float, y: Float) {
        params.lightDirX = x
        params.lightDirY = y
    }

    fun update(delta: Float, cumulativeScrollX: Float, cumulativeScrollY: Float) {
        time += delta
        scrollX = cumulativeScrollX
        scrollY = cumulativeScrollY

        // Animate tint if enabled (cool→warm oscillation)
        if (params.colorAnimSpeed > 0) {
            val t = sin((time * params.colorAnimSpeed).toDouble()).toFloat() * 0.5f + 0.5f
            // Lerp from cool blue to warm orange
            params.tintR = 0.8f + t * 0.4f
            params.tintG = 0.9f + t * 0.1f
            params.tintB = 1.1f - t * 0.3f
        }
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera) {
        if (!shader.isCompiled) return

        batch.shader = shader
        shader.bind()
        shader.setUniformf("u_time", time)
        shader.setUniformf("u_resolution", Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        shader.setUniformf("u_scroll", scrollX, scrollY)
//        shader.setUniformf("u_tint", params.tintR, params.tintG, params.tintB)
        shader.setUniformf("u_intensity", params.intensity)
        shader.setUniformf("u_noiseScale", params.noiseScale)
        shader.setUniformf("u_scrollSpeed", params.scrollSpeed)
        shader.setUniformf("u_contrast", params.contrast)
        shader.setUniformf("u_lightDir", params.lightDirX, params.lightDirY)

        val x = camera.position.x - camera.viewportWidth * camera.zoom / 2
        val y = camera.position.y - camera.viewportHeight * camera.zoom / 2
        val w = camera.viewportWidth * camera.zoom
        val h = camera.viewportHeight * camera.zoom

        batch.draw(emptyTexture, x, y, w, h)
        batch.shader = null
    }

    override fun dispose() {
        shader.dispose()
        emptyTexture.dispose()
    }
}
