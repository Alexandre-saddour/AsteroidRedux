package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D

import com.example.asteroidsredux.progression.PlayerStats

class Bullet {
    val position = Vector2()
    val velocity = Vector2()
    var distanceTraveled = 0f
    var maxDistance = 0f
    var active = false

    var damage = 1f // todo: not used MAIS COMMENT SA MARCHE ??
    var remainingHits = 0 // todo: not used
    var size = Constants.BULLET_SIZE

    fun init(x: Float, y: Float, angle: Float, stats: PlayerStats) {
        position.set(x, y)
        velocity.set(stats.bulletSpeed, 0f).rotateRad(angle)
        
        distanceTraveled = 0f
        maxDistance = Constants.BULLET_MAX_DISTANCE * stats.bulletLifetimeMultiplier
        
        active = true

        damage = stats.baseDamage
        remainingHits = stats.pierceLevel
        size = Constants.BULLET_SIZE * stats.bulletSizeMultiplier
    }

    fun update(delta: Float) {
        if (!active) return
        
        // Calculate distance moved this frame BEFORE wrapping
        val distanceThisFrame = velocity.len() * delta
        distanceTraveled += distanceThisFrame
        
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        if (distanceTraveled >= maxDistance) {
            active = false
        }
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (!active) return
        shapeRenderer.color = Constants.BULLET_COLOR
        shapeRenderer.circle(position.x, position.y, size)
    }
}
