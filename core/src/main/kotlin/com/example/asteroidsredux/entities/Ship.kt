package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D

import com.example.asteroidsredux.progression.PlayerStats

class Ship(private val inputHandler: InputHandler, private val stats: PlayerStats, private val texture: Texture) {
    val position = Vector2(Constants.WORLD_WIDTH / 2, Constants.WORLD_HEIGHT / 2)
    val velocity = Vector2()
    var angle = MathUtils.PI / 2 // Pointing up
    val nose = Vector2()
    var isDead = false

    fun update(delta: Float) {
        if (isDead) return

        // Rotation - rotate towards joystick direction if active
        val targetAngle = inputHandler.targetAngle
        if (targetAngle != null) {
            // Calculate shortest rotation direction
            var angleDiff = targetAngle - angle
            
            // Normalize to [-PI, PI]
            while (angleDiff > MathUtils.PI) angleDiff -= MathUtils.PI2
            while (angleDiff < -MathUtils.PI) angleDiff += MathUtils.PI2
            
            // Rotate towards target at rotation speed
            val maxRotation = Constants.SHIP_ROTATION_SPEED * delta
            if (kotlin.math.abs(angleDiff) < maxRotation) {
                angle = targetAngle
            } else if (angleDiff > 0) {
                angle += maxRotation
            } else {
                angle -= maxRotation
            }
        }

        // Thrust
        if (inputHandler.isThrusting) {
            val thrust = Vector2(stats.shipAcceleration * delta, 0f).rotateRad(angle)
            velocity.add(thrust)
        } else {
            // Apply deceleration/friction when not thrusting
            val deceleration = 0.99f // Friction coefficient (lower = more friction)
            velocity.scl(deceleration)

            // Stop completely if velocity is very small
            if (velocity.len() < 1f) {
                velocity.set(0f, 0f)
            }
        }

        // Cap speed
        if (velocity.len() > stats.shipMaxSpeed) {
            velocity.setLength(stats.shipMaxSpeed)
        }

        // Move
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        // Update nose position for shooting
        nose.set(Constants.SHIP_SIZE, 0f).rotateRad(angle).add(position)
    }

    fun render(batch: SpriteBatch) {
        if (isDead) return
        
        // Draw texture centered on position, rotated by angle
        // angle is in radians, batch.draw takes degrees
        // Sprite points Up (presumably), so we need to subtract 90 degrees to align with 0 degrees (East)
        val degrees = (angle * MathUtils.radiansToDegrees) - 90f

        batch.draw(
            texture,
            position.x - Constants.SHIP_SIZE,
            position.y - Constants.SHIP_SIZE,
            Constants.SHIP_SIZE, // originX (center of rotation relative to x,y)
            Constants.SHIP_SIZE, // originY
            Constants.SHIP_SIZE * 2, // width
            Constants.SHIP_SIZE * 2, // height
            1f, // scaleX
            1f, // scaleY
            degrees,
            0,
            0,
            texture.width,
            texture.height,
            false,
            false
        )
    }
    
    // Keep shape renderer for debug or other uses if needed, but for now we replace it
    // Or we can overload if we want to support both, but let's stick to the plan.
}
