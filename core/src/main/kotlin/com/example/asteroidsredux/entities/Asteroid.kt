package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D

class Asteroid(val size: Float, x: Float, y: Float) {
    val position = Vector2(x, y)
    val velocity = MathUtils2D.randomVector(MathUtils.random(Constants.ASTEROID_SPEED_MIN, Constants.ASTEROID_SPEED_MAX))
    val polygon: Polygon
    var active = true

    init {
        // Generate random polygon shape
        val vertices = FloatArray(16) // 8 points
        val radius = size
        for (i in 0 until 8) {
            val angle = i * MathUtils.PI2 / 8
            val dist = radius * MathUtils.random(0.8f, 1.2f)
            vertices[i * 2] = MathUtils.cos(angle) * dist
            vertices[i * 2 + 1] = MathUtils.sin(angle) * dist
        }
        polygon = Polygon(vertices)
    }
    
    val xpValue: Int
        get() = when (size) {
            Constants.ASTEROID_SIZE_LARGE -> Constants.XP_LARGE
            Constants.ASTEROID_SIZE_MEDIUM -> Constants.XP_MEDIUM
            Constants.ASTEROID_SIZE_SMALL -> Constants.XP_SMALL
            else -> 0
        }

    fun update(delta: Float) {
        if (!active) return
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        polygon.setPosition(position.x, position.y)
        polygon.rotate(50 * delta) // Simple rotation
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (!active) return
        shapeRenderer.color = Constants.ASTEROID_COLOR
        shapeRenderer.polygon(polygon.transformedVertices)
    }

    fun render(batch: com.badlogic.gdx.graphics.g2d.SpriteBatch, texture: com.badlogic.gdx.graphics.Texture) {
        if (!active) return
        com.example.asteroidsredux.utils.SpriteRenderer.drawCentered(
            batch,
            texture,
            position.x,
            position.y,
            size,
            polygon.rotation
        )
    }
}
