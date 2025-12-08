package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets

/**
 * Production-ready debris renderer with velocity drift and depth-based effects.
 */
class DebrisRenderer(private val assets: Assets) : Disposable {

    private val pixelRegion: TextureRegion by lazy { assets.getPixelRegion() }

    /** Runtime tuning parameters */
    data class DebrisParams(
        var density: Float = 1f,           // Multiplier for visibility
        var alphaFalloff: Float = 0.3f,    // Edge fade intensity
        var motionVariance: Float = 1f,    // Velocity variation multiplier
        var depthBrightness: Float = 0.3f  // Extra brightness for near particles
    )

    val params = DebrisParams()

    /** Debris particle with velocity */
    data class Debris(
        var x: Float,
        var y: Float,
        val width: Float,
        val height: Float,
        val speed: Float,
        var rotation: Float,
        val rotationSpeed: Float,
        val velocityX: Float,  // Drift velocity
        val velocityY: Float,
        val r: Float, val g: Float, val b: Float
    )

    private val debrisList = ArrayList<Debris>()
    private val DEBRIS_COUNT = 80
    private val TILE_SIZE = 2000f

    var lastDrawCount = 0
        private set

    init {
        for (i in 0 until DEBRIS_COUNT) {
            val baseSize = MathUtils.random(1f, 2.5f)
            val elongation = MathUtils.random(2f, 4f)
            val brightness = MathUtils.random(0.5f, 0.8f)
            val blueTint = MathUtils.random(0.9f, 1.1f)

            debrisList.add(Debris(
                x = MathUtils.random(0f, TILE_SIZE),
                y = MathUtils.random(0f, TILE_SIZE),
                width = baseSize,
                height = baseSize * elongation,
                speed = MathUtils.random(1.2f, 1.6f),
                rotation = MathUtils.random(0f, 360f),
                rotationSpeed = MathUtils.random(-60f, 60f),
                velocityX = MathUtils.random(-5f, 5f),   // Drift per second
                velocityY = MathUtils.random(-5f, 5f),
                r = brightness,
                g = brightness,
                b = brightness * blueTint
            ))
        }
    }

    private var time = 0f
    private var scrollX = 0f
    private var scrollY = 0f

    fun update(delta: Float, time: Float, cumulativeScrollX: Float, cumulativeScrollY: Float) {
        this.time = time
        this.scrollX = cumulativeScrollX
        this.scrollY = cumulativeScrollY

        val variance = params.motionVariance
        for (debris in debrisList) {
            debris.rotation += debris.rotationSpeed * delta * variance
            // Apply drift velocity
            debris.x += debris.velocityX * delta * variance
            debris.y += debris.velocityY * delta * variance
            // Wrap within tile
            debris.x = ((debris.x % TILE_SIZE) + TILE_SIZE) % TILE_SIZE
            debris.y = ((debris.y % TILE_SIZE) + TILE_SIZE) % TILE_SIZE
        }
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera, alphaMultiplier: Float = 1f) {
        lastDrawCount = 0

        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom

        val virtualCamX = scrollX
        val virtualCamY = scrollY

        val minTileX = kotlin.math.floor((virtualCamX - viewW) / TILE_SIZE).toInt() - 1
        val maxTileX = kotlin.math.floor((virtualCamX + viewW) / TILE_SIZE).toInt() + 1
        val minTileY = kotlin.math.floor((virtualCamY - viewH) / TILE_SIZE).toInt() - 1
        val maxTileY = kotlin.math.floor((virtualCamY + viewH) / TILE_SIZE).toInt() + 1

        val density = params.density
        val falloff = params.alphaFalloff
        val depthBoost = params.depthBrightness

        for (debris in debrisList) {
            val layerOffsetX = scrollX * debris.speed
            val layerOffsetY = scrollY * debris.speed

            // Depth-based brightness: higher parallax speed = closer = brighter
            val depthFactor = 1f + (debris.speed - 1.2f) * depthBoost

            for (tileX in minTileX..maxTileX) {
                for (tileY in minTileY..maxTileY) {
                    val baseTileX = tileX * TILE_SIZE
                    val baseTileY = tileY * TILE_SIZE

                    val worldX = baseTileX + debris.x + layerOffsetX - virtualCamX + camX
                    val worldY = baseTileY + debris.y + layerOffsetY - virtualCamY + camY

                    val maxDim = maxOf(debris.width, debris.height)
                    if (worldX < camX - viewW / 2 - maxDim || worldX > camX + viewW / 2 + maxDim) continue
                    if (worldY < camY - viewH / 2 - maxDim || worldY > camY + viewH / 2 + maxDim) continue

                    // Edge fade
                    val distFromCenter = kotlin.math.sqrt(
                        ((worldX - camX) / viewW * 2).toDouble().let { it * it } +
                        ((worldY - camY) / viewH * 2).toDouble().let { it * it }
                    ).toFloat()
                    val edgeFade = (1f - distFromCenter * falloff).coerceIn(0.3f, 1f)
                    val alpha = 0.6f * edgeFade * alphaMultiplier * density

                    // Apply depth brightness
                    val r = (debris.r * depthFactor).coerceAtMost(1f)
                    val g = (debris.g * depthFactor).coerceAtMost(1f)
                    val b = (debris.b * depthFactor).coerceAtMost(1f)

                    batch.setColor(r, g, b, alpha)
                    batch.draw(
                        pixelRegion,
                        worldX - debris.width / 2,
                        worldY - debris.height / 2,
                        debris.width / 2,
                        debris.height / 2,
                        debris.width,
                        debris.height,
                        1f, 1f,
                        debris.rotation
                    )
                    lastDrawCount++
                }
            }
        }

        batch.setColor(Color.WHITE)
    }

    override fun dispose() {
    }
}
