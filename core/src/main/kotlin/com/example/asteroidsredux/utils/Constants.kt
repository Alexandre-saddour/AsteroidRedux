package com.example.asteroidsredux.utils

import com.badlogic.gdx.graphics.Color

object Constants {
    // World dimensions (virtual units) - Scaled up to match user's physics values (e.g. speed 500)
    var WORLD_WIDTH = 1280f
    var WORLD_HEIGHT = 720f

//    const val WORLD_WIDTH = 100f
//    const val WORLD_HEIGHT = 60f // 16:9 aspect ratio roughly

    // Colors
    val BACKGROUND_COLOR = Color(0f, 0f, 0f, 1f)
    val SHIP_COLOR = Color(1f, 1f, 1f, 1f)
    val ASTEROID_COLOR = Color(0.8f, 0.8f, 0.8f, 1f)
    val BULLET_COLOR = Color(0f, 1f, 1f, 1f) // Cyan
    val PARTICLE_COLOR = Color(1f, 1f, 1f, 1f)

    // Ship
    const val SHIP_SIZE = 20f
    const val SHIP_ROTATION_SPEED = 5f // Radians per second

    // Bullet
    const val BULLET_LIFETIME = 2.0f // Seconds
    const val BULLET_SIZE = 5f

    // Asteroids
    const val ASTEROID_SPEED_MIN = 30f
    const val ASTEROID_SPEED_MAX = 100f
    const val ASTEROID_SIZE_LARGE = 60f
    const val ASTEROID_SIZE_MEDIUM = 30f
    const val ASTEROID_SIZE_SMALL = 15f

    // Scores
    const val ASTEROID_SCORE_LARGE = 20
    const val ASTEROID_SCORE_MEDIUM = 50
    const val ASTEROID_SCORE_SMALL = 100

    // XP Rewards
    const val XP_LARGE = 10
    const val XP_MEDIUM = 6
    const val XP_SMALL = 3

    // Particles
    const val PARTICLE_LIFETIME = 0.5f
    const val PARTICLE_SPEED = 100f
}
