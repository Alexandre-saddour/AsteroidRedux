package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import kotlin.math.sin

/**
 * Production-ready light overlay renderer with pulsing, color temperature, and rotation.
 */
class LightOverlayRenderer(private val assets: Assets) : Disposable {

    private val haloTexture: Texture by lazy { assets.getHaloTexture() }
    private val cookieTexture: Texture by lazy { assets.getLightCookieTexture() }

    /** Runtime parameters */
    data class LightParams(
        var globalLightR: Float = 1f,
        var globalLightG: Float = 1f,
        var globalLightB: Float = 1f,
        var pulseSpeed: Float = 2f,        // Pulse animation speed
        var pulseAmplitude: Float = 0.15f, // Pulse intensity variation
        var cookieRotationSpeed: Float = 3f // Cookie rotation degrees/sec
    )

    val params = LightParams()

    /** Halo types for color temperature */
    enum class HaloType {
        PLAYER,     // Cold blue
        EXPLOSION,  // Warm orange
        NEUTRAL     // Uses global color
    }

    /** Halo instance */
    data class Halo(
        var x: Float,
        var y: Float,
        var size: Float,
        val type: HaloType,
        var rotation: Float,
        val rotationSpeed: Float,
        var lifetime: Float,
        val fadeIn: Float = 0.2f,
        val fadeOut: Float = 0.4f,
        var pulsePhase: Float = 0f
    ) {
        var age = 0f
    }

    private val halos = ArrayList<Halo>()
    private val toRemove = ArrayList<Halo>()
    private var cookieRotation = 0f

    // Player halo (always present)
    private val playerHalo = Halo(
        x = 0f, y = 0f,
        size = 400f,
        type = HaloType.PLAYER,
        rotation = 0f,
        rotationSpeed = 5f,
        lifetime = -1f,
        pulsePhase = 0f
    )

    var lastDrawCount = 0
        private set

    private var time = 0f

    init {
        halos.add(playerHalo)
    }

    /** Add a temporary halo */
    fun addHalo(x: Float, y: Float, size: Float, color: Color, lifetime: Float = 1.5f) {
        halos.add(Halo(
            x = x,
            y = y,
            size = size,
            type = HaloType.EXPLOSION,
            rotation = 0f,
            rotationSpeed = 15f,
            lifetime = lifetime,
            pulsePhase = time * params.pulseSpeed
        ))
    }

    /** Set global light color for tone adaptation */
    fun setGlobalLightColor(r: Float, g: Float, b: Float) {
        params.globalLightR = r
        params.globalLightG = g
        params.globalLightB = b
    }

    fun update(delta: Float, time: Float) {
        this.time = time
        cookieRotation += params.cookieRotationSpeed * delta

        toRemove.clear()
        for (halo in halos) {
            halo.rotation += halo.rotationSpeed * delta
            halo.age += delta

            if (halo.lifetime > 0 && halo.age >= halo.lifetime + halo.fadeOut) {
                toRemove.add(halo)
            }
        }
        halos.removeAll(toRemove)
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera) {
        lastDrawCount = 0

        // Update player halo position
        playerHalo.x = camera.position.x
        playerHalo.y = camera.position.y

        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom

        // Save blend state
        val originalBlendSrc = batch.blendSrcFunc
        val originalBlendDst = batch.blendDstFunc

        // Additive blending
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        // Render halos
        for (halo in halos) {
            // Calculate base color from type
            val (baseR, baseG, baseB, baseAlpha) = when (halo.type) {
                HaloType.PLAYER -> floatArrayOf(0.1f, 0.25f, 0.4f, 0.35f)
                HaloType.EXPLOSION -> floatArrayOf(1f, 0.5f, 0.2f, 0.6f)
                HaloType.NEUTRAL -> floatArrayOf(
                    params.globalLightR * 0.3f,
                    params.globalLightG * 0.3f,
                    params.globalLightB * 0.3f,
                    0.4f
                )
            }

            // Apply global tint
            val r = baseR * params.globalLightR
            val g = baseG * params.globalLightG
            val b = baseB * params.globalLightB

            // Calculate alpha with fade and pulse
            var alpha = baseAlpha
            if (halo.lifetime > 0) {
                if (halo.age < halo.fadeIn) {
                    alpha *= halo.age / halo.fadeIn
                } else if (halo.age > halo.lifetime) {
                    alpha *= 1f - (halo.age - halo.lifetime) / halo.fadeOut
                }
            }

            // Soft pulsing
            val pulse = (1f - params.pulseAmplitude) + params.pulseAmplitude *
                sin((time * params.pulseSpeed + halo.pulsePhase).toDouble()).toFloat()
            alpha *= pulse

            batch.setColor(r, g, b, alpha)
            batch.draw(
                haloTexture,
                halo.x - halo.size / 2,
                halo.y - halo.size / 2,
                halo.size / 2,
                halo.size / 2,
                halo.size,
                halo.size,
                1f, 1f,
                halo.rotation,
                0, 0,
                haloTexture.width,
                haloTexture.height,
                false, false
            )
            lastDrawCount++
        }

        // Light cookie with rotation drift
        batch.setColor(
            0.05f * params.globalLightR,
            0.05f * params.globalLightG,
            0.08f * params.globalLightB,
            0.08f
        )
        batch.draw(
            cookieTexture,
            camX - viewW / 2,
            camY - viewH / 2,
            viewW / 2,
            viewH / 2,
            viewW,
            viewH,
            1f, 1f,
            cookieRotation,
            0, 0,
            cookieTexture.width,
            cookieTexture.height,
            false, false
        )
        lastDrawCount++

        // Restore blend state
        batch.setColor(Color.WHITE)
        batch.setBlendFunction(originalBlendSrc, originalBlendDst)
    }

    override fun dispose() {
    }
}
