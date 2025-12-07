package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable

/**
 * Renders a dense, lively star field with per-layer parallax.
 * Uses cumulative scroll tracking to avoid jumps when camera wraps.
 */
class StarsRenderer : Disposable {
    private val shapeRenderer = ShapeRenderer()

    data class Star(
        val x: Float,  // Position in tile (0..TILE_SIZE)
        val y: Float,
        val size: Float,
        val brightness: Float,
        val color: Color
    )

    data class StarLayer(
        val stars: List<Star>,
        val parallaxFactor: Float,  // How much this layer moves relative to camera (0 = static, 1 = moves with camera)
        val tileSize: Float
    )

    private val layers = ArrayList<StarLayer>()

    init {
        // Layer 1: Dense tiny background stars - almost no parallax
        layers.add(createLayer(
            count = 4000,
            sizeRange = 0.3f to 1f,
            brightnessRange = 0.1f to 0.35f,
            colorWeights = mapOf("white" to 0.9f, "blue" to 0.05f, "red" to 0.03f, "yellow" to 0.02f),
            parallaxFactor = 0.02f,
            tileSize = 1500f
        ))

        // Layer 2: Small visible stars - slight parallax
        layers.add(createLayer(
            count = 2000,
            sizeRange = 0.8f to 1.8f,
            brightnessRange = 0.25f to 0.5f,
            colorWeights = mapOf("white" to 0.7f, "blue" to 0.15f, "red" to 0.1f, "yellow" to 0.05f),
            parallaxFactor = 0.05f,
            tileSize = 1500f
        ))

        // Layer 3: Medium stars - noticeable parallax
        layers.add(createLayer(
            count = 800,
            sizeRange = 1.5f to 3f,
            brightnessRange = 0.4f to 0.7f,
            colorWeights = mapOf("white" to 0.5f, "blue" to 0.25f, "red" to 0.15f, "yellow" to 0.1f),
            parallaxFactor = 0.1f,
            tileSize = 1500f
        ))

        // Layer 4: Large prominent stars - clear parallax
        layers.add(createLayer(
            count = 150,
            sizeRange = 3f to 5f,
            brightnessRange = 0.6f to 0.85f,
            colorWeights = mapOf("white" to 0.4f, "blue" to 0.3f, "red" to 0.2f, "yellow" to 0.1f),
            parallaxFactor = 0.18f,
            tileSize = 1500f
        ))

        // Layer 5: Very large bright stars - strong parallax
        layers.add(createLayer(
            count = 40,
            sizeRange = 5f to 8f,
            brightnessRange = 0.8f to 1f,
            colorWeights = mapOf("white" to 0.3f, "blue" to 0.35f, "red" to 0.25f, "yellow" to 0.1f),
            parallaxFactor = 0.25f,
            tileSize = 1500f
        ))

        // Layer 6: Giant stars (rare) - strongest parallax
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
        val stars = (0 until count).map {
            createStar(sizeRange, brightnessRange, colorWeights, tileSize)
        }
        return StarLayer(stars, parallaxFactor, tileSize)
    }

    private fun createStar(
        sizeRange: Pair<Float, Float>,
        brightnessRange: Pair<Float, Float>,
        colorWeights: Map<String, Float>,
        tileSize: Float
    ): Star {
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

        val color = when (selectedColor) {
            "red" -> Color(
                MathUtils.random(0.9f, 1f),
                MathUtils.random(0.3f, 0.5f),
                MathUtils.random(0.2f, 0.4f),
                1f
            )
            "blue" -> Color(
                MathUtils.random(0.5f, 0.7f),
                MathUtils.random(0.7f, 0.9f),
                MathUtils.random(0.95f, 1f),
                1f
            )
            "yellow" -> Color(
                MathUtils.random(0.95f, 1f),
                MathUtils.random(0.9f, 1f),
                MathUtils.random(0.5f, 0.7f),
                1f
            )
            else -> Color(
                MathUtils.random(0.9f, 1f),
                MathUtils.random(0.9f, 1f),
                MathUtils.random(0.9f, 1f),
                1f
            )
        }

        return Star(
            x = MathUtils.random(0f, tileSize),
            y = MathUtils.random(0f, tileSize),
            size = MathUtils.random(sizeRange.first, sizeRange.second),
            brightness = MathUtils.random(brightnessRange.first, brightnessRange.second),
            color = color
        )
    }

    fun update(delta: Float, camera: OrthographicCamera) {
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera, cumulativeScrollX: Float, cumulativeScrollY: Float) {
        batch.end()

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom

        for (layer in layers) {
            val tileSize = layer.tileSize

            // Layer parallax offset based on cumulative scroll
            val layerOffsetX = cumulativeScrollX * layer.parallaxFactor
            val layerOffsetY = cumulativeScrollY * layer.parallaxFactor

            // Use cumulative scroll for tile positioning (not camera position)
            // This ensures tiles don't jump when camera wraps
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

                    for (star in layer.stars) {
                        // Star position = tile base + star local + parallax shift
                        // Then transform to screen-relative position
                        val worldX = baseTileX + star.x + layerOffsetX - virtualCamX + camX
                        val worldY = baseTileY + star.y + layerOffsetY - virtualCamY + camY

                        if (worldX >= camX - viewW/2 - star.size && worldX <= camX + viewW/2 + star.size &&
                            worldY >= camY - viewH/2 - star.size && worldY <= camY + viewH/2 + star.size) {

                            shapeRenderer.setColor(star.color.r, star.color.g, star.color.b, star.brightness)
                            shapeRenderer.circle(worldX, worldY, star.size * 0.5f)
                        }
                    }
                }
            }
        }

        shapeRenderer.end()
        batch.begin()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
