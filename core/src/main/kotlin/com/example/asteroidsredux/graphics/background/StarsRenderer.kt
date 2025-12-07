package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable

/**
 * Renders a dense, lively star field inspired by real space imagery.
 * Uses cumulative scroll tracking to avoid jumps when camera wraps.
 */
class StarsRenderer : Disposable {
    private val shapeRenderer = ShapeRenderer()
    
    data class Star(
        val x: Float,
        val y: Float,
        val size: Float,
        val brightness: Float,
        val color: Color
    )
    
    private val stars = ArrayList<Star>()
    private val TILE_SIZE = 1500f
    
    init {
        // === LAYER 1: Dense background of tiny dim stars ===
        repeat(5000) {
            stars.add(createStar(
                sizeRange = 0.3f to 1f,
                brightnessRange = 0.1f to 0.35f,
                colorWeights = mapOf("white" to 0.9f, "blue" to 0.05f, "red" to 0.03f, "yellow" to 0.02f)
            ))
        }
        
        // === LAYER 2: Small visible stars ===
        repeat(2500) {
            stars.add(createStar(
                sizeRange = 0.8f to 1.8f,
                brightnessRange = 0.25f to 0.5f,
                colorWeights = mapOf("white" to 0.7f, "blue" to 0.15f, "red" to 0.1f, "yellow" to 0.05f)
            ))
        }
        
        // === LAYER 3: Medium stars ===
        repeat(1000) {
            stars.add(createStar(
                sizeRange = 1.5f to 3f,
                brightnessRange = 0.4f to 0.7f,
                colorWeights = mapOf("white" to 0.5f, "blue" to 0.25f, "red" to 0.15f, "yellow" to 0.1f)
            ))
        }
        
        // === LAYER 4: Large prominent stars ===
        repeat(200) {
            stars.add(createStar(
                sizeRange = 3f to 5f,
                brightnessRange = 0.6f to 0.85f,
                colorWeights = mapOf("white" to 0.4f, "blue" to 0.3f, "red" to 0.2f, "yellow" to 0.1f)
            ))
        }
        
        // === LAYER 5: Very large bright stars ===
        repeat(50) {
            stars.add(createStar(
                sizeRange = 5f to 8f,
                brightnessRange = 0.8f to 1f,
                colorWeights = mapOf("white" to 0.3f, "blue" to 0.35f, "red" to 0.25f, "yellow" to 0.1f)
            ))
        }
        
        // === LAYER 6: Giant stars (rare) ===
        repeat(10) {
            stars.add(createStar(
                sizeRange = 8f to 12f,
                brightnessRange = 0.9f to 1f,
                colorWeights = mapOf("white" to 0.2f, "blue" to 0.4f, "red" to 0.3f, "yellow" to 0.1f)
            ))
        }
    }
    
    private fun createStar(
        sizeRange: Pair<Float, Float>,
        brightnessRange: Pair<Float, Float>,
        colorWeights: Map<String, Float>
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
            x = MathUtils.random(0f, TILE_SIZE),
            y = MathUtils.random(0f, TILE_SIZE),
            size = MathUtils.random(sizeRange.first, sizeRange.second),
            brightness = MathUtils.random(brightnessRange.first, brightnessRange.second),
            color = color
        )
    }

    fun update(delta: Float, camera: OrthographicCamera) {
        // No update needed
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera, cumulativeScrollX: Float, cumulativeScrollY: Float) {
        batch.end()
        
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom
        
        // Use cumulative scroll for tile offset calculation to avoid jumping
        val scrollOffsetX = cumulativeScrollX * 0.02f // Very slow parallax
        val scrollOffsetY = cumulativeScrollY * 0.02f
        
        // Calculate tile indices based on camera position (for visibility) but offset by scroll
        val minTileX = kotlin.math.floor((camX - viewW/2 - scrollOffsetX) / TILE_SIZE).toInt() - 1
        val maxTileX = kotlin.math.floor((camX + viewW/2 - scrollOffsetX) / TILE_SIZE).toInt() + 1
        val minTileY = kotlin.math.floor((camY - viewH/2 - scrollOffsetY) / TILE_SIZE).toInt() - 1
        val maxTileY = kotlin.math.floor((camY + viewH/2 - scrollOffsetY) / TILE_SIZE).toInt() + 1
        
        for (tileX in minTileX..maxTileX) {
            for (tileY in minTileY..maxTileY) {
                val tileOffsetX = tileX * TILE_SIZE + scrollOffsetX
                val tileOffsetY = tileY * TILE_SIZE + scrollOffsetY
                
                for (star in stars) {
                    val worldX = tileOffsetX + star.x
                    val worldY = tileOffsetY + star.y
                    
                    if (worldX >= camX - viewW/2 - star.size && worldX <= camX + viewW/2 + star.size &&
                        worldY >= camY - viewH/2 - star.size && worldY <= camY + viewH/2 + star.size) {
                        
                        shapeRenderer.setColor(star.color.r, star.color.g, star.color.b, star.brightness)
                        shapeRenderer.circle(worldX, worldY, star.size * 0.5f)
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
