package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import kotlin.math.sin

/**
 * Production-ready star field renderer with GPU batching.
 * Features per-layer parallax, twinkle animation, color drift, and runtime tuning.
 */
class StarsRenderer(private val assets: Assets) : Disposable {

    private val pixelRegion: TextureRegion by lazy { assets.getPixelRegion() }

    /** Runtime tuning parameters */
    data class StarParams(
        var parallaxScale: Float = 1f,        // Multiplier for all parallax factors
        var twinkleAmplitude: Float = 0.2f,   // How much stars twinkle (0–0.5)
        var twinkleSpeedMin: Float = 0.5f,    // Min twinkle speed
        var twinkleSpeedMax: Float = 2f,      // Max twinkle speed
        var colorDriftAmount: Float = 0.05f,  // Hue oscillation intensity
        var colorDriftSpeed: Float = 0.3f,    // Hue oscillation speed
        var globalTintR: Float = 1f,          // Global color tint R
        var globalTintG: Float = 1f,          // Global color tint G
        var globalTintB: Float = 1f           // Global color tint B
    )

    val params = StarParams()

    /** Layer configuration with pre-allocated star array */
    data class StarLayer(
        val stars: FloatArray,     // Packed: [x, y, size, brightness, r, g, b, phase, speed, driftPhase] × count
        val parallaxFactor: Float,
        val tileSize: Float,
        val starCount: Int
    ) {
        companion object {
            const val FLOATS_PER_STAR = 10  // Added driftPhase
        }
    }

    private val layers = ArrayList<StarLayer>()
    var lastDrawCount = 0
        private set

    init {
        // Layer 1: Dense tiny background stars - almost no parallax
        layers.add(createLayer(
            count = 3000,
            sizeRange = 0.5f to 1.2f,
            brightnessRange = 0.15f to 0.4f,
            colorWeights = mapOf("white" to 0.85f, "blue" to 0.08f, "red" to 0.04f, "yellow" to 0.03f),
            parallaxFactor = 0.02f,
            tileSize = 1500f
        ))

        // Layer 2: Small visible stars
        layers.add(createLayer(
            count = 1500,
            sizeRange = 1f to 2f,
            brightnessRange = 0.3f to 0.55f,
            colorWeights = mapOf("white" to 0.65f, "blue" to 0.18f, "red" to 0.1f, "yellow" to 0.07f),
            parallaxFactor = 0.05f,
            tileSize = 1500f
        ))

        // Layer 3: Medium stars
        layers.add(createLayer(
            count = 600,
            sizeRange = 1.8f to 3.2f,
            brightnessRange = 0.45f to 0.75f,
            colorWeights = mapOf("white" to 0.5f, "blue" to 0.25f, "red" to 0.15f, "yellow" to 0.1f),
            parallaxFactor = 0.1f,
            tileSize = 1500f
        ))

        // Layer 4: Large prominent stars
        layers.add(createLayer(
            count = 120,
            sizeRange = 3f to 5f,
            brightnessRange = 0.6f to 0.9f,
            colorWeights = mapOf("white" to 0.4f, "blue" to 0.3f, "red" to 0.2f, "yellow" to 0.1f),
            parallaxFactor = 0.18f,
            tileSize = 1500f
        ))

        // Layer 5: Very large bright stars
        layers.add(createLayer(
            count = 30,
            sizeRange = 5f to 8f,
            brightnessRange = 0.75f to 1f,
            colorWeights = mapOf("white" to 0.3f, "blue" to 0.35f, "red" to 0.25f, "yellow" to 0.1f),
            parallaxFactor = 0.25f,
            tileSize = 1500f
        ))

        // Layer 6: Giant stars (rare)
        layers.add(createLayer(
            count = 8,
            sizeRange = 8f to 12f,
            brightnessRange = 0.9f to 1f,
            colorWeights = mapOf("white" to 0.2f, "blue" to 0.4f, "red" to 0.3f, "yellow" to 0.1f),
            parallaxFactor = 0.35f,
            tileSize = 1500f
        ))
    }

    private fun createLayer(
        count: Int,
        sizeRange: Pair<Float, Float>,
        brightnessRange: Pair<Float, Float>,
        colorWeights: Map<String, Float>,
        parallaxFactor: Float,
        tileSize: Float
    ): StarLayer {
        val stars = FloatArray(count * StarLayer.FLOATS_PER_STAR)

        for (i in 0 until count) {
            val baseIdx = i * StarLayer.FLOATS_PER_STAR

            // Position
            stars[baseIdx + 0] = MathUtils.random(0f, tileSize)
            stars[baseIdx + 1] = MathUtils.random(0f, tileSize)

            // Size and brightness
            stars[baseIdx + 2] = MathUtils.random(sizeRange.first, sizeRange.second)
            stars[baseIdx + 3] = MathUtils.random(brightnessRange.first, brightnessRange.second)

            // Color selection
            val roll = MathUtils.random()
            var cumulative = 0f
            var selectedColor = "white"
            for ((colorName, weight) in colorWeights) {
                cumulative += weight
                if (roll <= cumulative) {
                    selectedColor = colorName
                    break
                }
            }

            val (r, g, b) = when (selectedColor) {
                "red" -> Triple(
                    MathUtils.random(0.9f, 1f),
                    MathUtils.random(0.3f, 0.5f),
                    MathUtils.random(0.2f, 0.4f)
                )
                "blue" -> Triple(
                    MathUtils.random(0.5f, 0.7f),
                    MathUtils.random(0.7f, 0.9f),
                    MathUtils.random(0.95f, 1f)
                )
                "yellow" -> Triple(
                    MathUtils.random(0.95f, 1f),
                    MathUtils.random(0.9f, 1f),
                    MathUtils.random(0.5f, 0.7f)
                )
                else -> Triple(
                    MathUtils.random(0.9f, 1f),
                    MathUtils.random(0.9f, 1f),
                    MathUtils.random(0.9f, 1f)
                )
            }

            stars[baseIdx + 4] = r
            stars[baseIdx + 5] = g
            stars[baseIdx + 6] = b

            // Twinkle animation parameters
            stars[baseIdx + 7] = MathUtils.random(0f, MathUtils.PI2)  // phase
            stars[baseIdx + 8] = MathUtils.random(0.5f, 2f)           // speed

            // Color drift phase (unique per star for varied shimmer)
            stars[baseIdx + 9] = MathUtils.random(0f, MathUtils.PI2)
        }

        return StarLayer(stars, parallaxFactor, tileSize, count)
    }

    /** Set global tint for color grading */
    fun setGlobalTint(r: Float, g: Float, b: Float) {
        params.globalTintR = r
        params.globalTintG = g
        params.globalTintB = b
    }

    fun update(delta: Float, time: Float) {
        // No per-frame work needed - all animation computed in render
    }

    fun render(
        batch: SpriteBatch,
        camera: OrthographicCamera,
        cumulativeScrollX: Float,
        cumulativeScrollY: Float,
        brightnessMultiplier: Float = 1f
    ) {
        lastDrawCount = 0

        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom
        val time = com.example.asteroidsredux.AsteroidsGame.stateTime

        // Pre-compute params
        val parallaxScale = params.parallaxScale
        val twinkleAmp = params.twinkleAmplitude
        val twinkleBase = 1f - twinkleAmp
        val driftAmount = params.colorDriftAmount
        val driftSpeed = params.colorDriftSpeed
        val tintR = params.globalTintR
        val tintG = params.globalTintG
        val tintB = params.globalTintB

        for (layer in layers) {
            val tileSize = layer.tileSize
            val stars = layer.stars

            // Layer parallax offset (scaled by params)
            val layerOffsetX = cumulativeScrollX * layer.parallaxFactor * parallaxScale
            val layerOffsetY = cumulativeScrollY * layer.parallaxFactor * parallaxScale

            // Virtual camera for tile positioning
            val virtualCamX = cumulativeScrollX
            val virtualCamY = cumulativeScrollY

            val minTileX = kotlin.math.floor((virtualCamX - viewW) / tileSize).toInt() - 1
            val maxTileX = kotlin.math.floor((virtualCamX + viewW) / tileSize).toInt() + 1
            val minTileY = kotlin.math.floor((virtualCamY - viewH) / tileSize).toInt() - 1
            val maxTileY = kotlin.math.floor((virtualCamY + viewH) / tileSize).toInt() + 1

            for (tileX in minTileX..maxTileX) {
                for (tileY in minTileY..maxTileY) {
                    val baseTileX = tileX * tileSize
                    val baseTileY = tileY * tileSize

                    for (i in 0 until layer.starCount) {
                        val idx = i * StarLayer.FLOATS_PER_STAR

                        val starX = stars[idx + 0]
                        val starY = stars[idx + 1]
                        val size = stars[idx + 2]
                        val baseBrightness = stars[idx + 3]
                        val baseR = stars[idx + 4]
                        val baseG = stars[idx + 5]
                        val baseB = stars[idx + 6]
                        val phase = stars[idx + 7]
                        val speed = stars[idx + 8]
                        val driftPhase = stars[idx + 9]

                        // World position with parallax
                        val worldX = baseTileX + starX + layerOffsetX - virtualCamX + camX
                        val worldY = baseTileY + starY + layerOffsetY - virtualCamY + camY

                        // Frustum culling
                        if (worldX < camX - viewW / 2 - size || worldX > camX + viewW / 2 + size) continue
                        if (worldY < camY - viewH / 2 - size || worldY > camY + viewH / 2 + size) continue

                        // Twinkle animation: alpha oscillates
                        val twinkle = twinkleBase + twinkleAmp * sin((time * speed + phase).toDouble()).toFloat()
                        val alpha = baseBrightness * twinkle * brightnessMultiplier

                        // Color drift: subtle hue shift over time
                        val drift = sin((time * driftSpeed + driftPhase).toDouble()).toFloat() * driftAmount
                        val r = (baseR + drift).coerceIn(0f, 1f) * tintR
                        val g = baseG * tintG
                        val b = (baseB - drift * 0.5f).coerceIn(0f, 1f) * tintB

                        // Draw star as scaled pixel
                        batch.setColor(r, g, b, alpha)
                        batch.draw(pixelRegion, worldX - size / 2, worldY - size / 2, size, size)
                        lastDrawCount++
                    }
                }
            }
        }

        // Reset batch color
        batch.setColor(Color.WHITE)
    }

    override fun dispose() {
        // No resources to dispose - uses shared pixel texture from Assets
    }
}
