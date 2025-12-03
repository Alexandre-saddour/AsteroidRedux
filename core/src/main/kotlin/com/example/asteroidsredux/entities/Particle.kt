package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.utils.Constants

class Particle {
    val position = Vector2()
    val velocity = Vector2()
    var lifetime = 0f
    var active = false

    fun init(x: Float, y: Float, vx: Float, vy: Float) {
        position.set(x, y)
        velocity.set(vx, vy)
        lifetime = Constants.PARTICLE_LIFETIME
        active = true
    }

    fun update(delta: Float) {
        if (!active) return
        position.mulAdd(velocity, delta)
        lifetime -= delta
        if (lifetime <= 0) {
            active = false
        }
    }

    fun render(shapeRenderer: ShapeRenderer) {
        if (!active) return
        shapeRenderer.setColor(Constants.PARTICLE_COLOR.r, Constants.PARTICLE_COLOR.g, Constants.PARTICLE_COLOR.b, lifetime / Constants.PARTICLE_LIFETIME)
        shapeRenderer.circle(position.x, position.y, 0.2f, 8)
    }
}
