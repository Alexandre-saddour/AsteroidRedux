package com.example.asteroidsredux.utils

import com.badlogic.gdx.graphics.Color

object Constants {
    // World dimensions (virtual units)
    const val WORLD_WIDTH = 100f
    const val WORLD_HEIGHT = 60f // 16:9 aspect ratio roughly

    // Colors
    val BACKGROUND_COLOR = Color(0f, 0f, 0f, 1f)
    val SHIP_COLOR = Color(1f, 1f, 1f, 1f)
    val ASTEROID_COLOR = Color(0.8f, 0.8f, 0.8f, 1f)
    val BULLET_COLOR = Color(0f, 1f, 1f, 1f) // Cyan
    val PARTICLE_COLOR = Color(1f, 1f, 1f, 1f)

    // Ship
    const val SHIP_SIZE = 2f
    const val SHIP_THRUST = 20f
    const val SHIP_ROTATION_SPEED = 5f // Radians per second
    const val SHIP_MAX_SPEED = 20f

    // Bullet
    const val BULLET_SPEED = 40f
    const val BULLET_LIFETIME = 1.5f // Seconds
    const val BULLET_SIZE = 0.5f

    // Asteroids
    const val ASTEROID_SPEED_MIN = 2f
    const val ASTEROID_SPEED_MAX = 8f
    const val ASTEROID_SIZE_LARGE = 6f
    const val ASTEROID_SIZE_MEDIUM = 3f
    const val ASTEROID_SIZE_SMALL = 1.5f
    const val ASTEROID_SCORE_LARGE = 50
    const val ASTEROID_SCORE_MEDIUM = 20
    const val ASTEROID_SCORE_SMALL = 10

    // Particles
    const val PARTICLE_LIFETIME = 0.5f
    const val PARTICLE_SPEED = 5f
}
