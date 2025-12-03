package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D

import com.example.asteroidsredux.progression.PlayerStats

class Bullet {
    val position = Vector2()
    val velocity = Vector2()
    var lifetime = 0f
    var active = false

    var damage = 1f // todo: not used MAIS COMMENT SA MARCHE ??
    var remainingHits = 0 // todo: not used

    fun init(x: Float, y: Float, angle: Float, stats: PlayerStats) {
        position.set(x, y)
        velocity.set(stats.bulletSpeed, 0f).rotateRad(angle)
        lifetime = Constants.BULLET_LIFETIME
        active = true

        damage = stats.baseDamage
        remainingHits = stats.pierceLevel
    }

    fun update(delta: Float) {
        if (!active) return
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        lifetime -= delta
        if (lifetime <= 0) {
            active = false
        }
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (!active) return
        shapeRenderer.color = Constants.BULLET_COLOR
        shapeRenderer.circle(position.x, position.y, Constants.BULLET_SIZE)
    }
}
