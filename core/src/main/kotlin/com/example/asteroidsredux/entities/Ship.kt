package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D

class Ship(private val inputHandler: InputHandler) {
    val position = Vector2(Constants.WORLD_WIDTH / 2, Constants.WORLD_HEIGHT / 2)
    val velocity = Vector2()
    var angle = MathUtils.PI / 2 // Pointing up
    val nose = Vector2()
    var isDead = false

    fun update(delta: Float) {
        if (isDead) return

        // Rotation
        if (inputHandler.isRotatingLeft) {
            angle += Constants.SHIP_ROTATION_SPEED * delta
        }
        if (inputHandler.isRotatingRight) {
            angle -= Constants.SHIP_ROTATION_SPEED * delta
        }

        // Thrust
        if (inputHandler.isThrusting) {
            val thrust = Vector2(Constants.SHIP_THRUST * delta, 0f).rotateRad(angle)
            velocity.add(thrust)
        }

        // Cap speed
        if (velocity.len() > Constants.SHIP_MAX_SPEED) {
            velocity.setLength(Constants.SHIP_MAX_SPEED)
        }

        // Move
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        // Update nose position for shooting
        nose.set(Constants.SHIP_SIZE, 0f).rotateRad(angle).add(position)
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (isDead) return
        shapeRenderer.color = Constants.SHIP_COLOR
        
        // Draw triangle
        val x1 = position.x + MathUtils.cos(angle) * Constants.SHIP_SIZE
        val y1 = position.y + MathUtils.sin(angle) * Constants.SHIP_SIZE
        
        val x2 = position.x + MathUtils.cos(angle + 2.5f) * Constants.SHIP_SIZE
        val y2 = position.y + MathUtils.sin(angle + 2.5f) * Constants.SHIP_SIZE
        
        val x3 = position.x + MathUtils.cos(angle - 2.5f) * Constants.SHIP_SIZE
        val y3 = position.y + MathUtils.sin(angle - 2.5f) * Constants.SHIP_SIZE

        shapeRenderer.triangle(x1, y1, x2, y2, x3, y3)
    }
}
