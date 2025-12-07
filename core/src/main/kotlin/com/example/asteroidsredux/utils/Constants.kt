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
    const val SHIP_SIZE = 25f
    const val SHIP_ROTATION_SPEED = 5f // Radians per second
    const val SHIP_SKIN_SCALE = 1.5f // Scale multiplier when using a skin
    val SHIP_GLOW_COLOR = Color.valueOf("20B2AA") // Light Sea Green
    const val SHIP_GLOW_SIZE_MULT = 1.2f // Glow size relative to ship size

    // Bullet
    const val BULLET_MAX_DISTANCE = 700f // Distance units
    const val BULLET_SIZE = 7f

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

    object UI {
        // Base reference dimensions for scaling
        private const val BASE_WIDTH = 1280f
        private const val BASE_HEIGHT = 720f
        
        // Global scale factor based on current world size
        val SCALE_FACTOR: Float get() = WORLD_HEIGHT / BASE_HEIGHT
        
        // Card Layout (dynamic)
        val CARD_WIDTH: Float get() = 300f * SCALE_FACTOR
        val CARD_HEIGHT: Float get() = 400f * SCALE_FACTOR
        val CARD_SPACING: Float get() = 40f * SCALE_FACTOR

        // Font Scales (dynamic)
        val FONT_SCALE_TITLE: Float get() = 4.5f * SCALE_FACTOR
        val FONT_SCALE_HEADER: Float get() = 2.5f * SCALE_FACTOR
        val FONT_SCALE_NORMAL: Float get() = 1.6f * SCALE_FACTOR
        val FONT_SCALE_SMALL: Float get() = 1.2f * SCALE_FACTOR
        val FONT_SCALE_BUTTON: Float get() = 2.5f * SCALE_FACTOR

        // Controls (dynamic)
        val JOYSTICK_RADIUS: Float get() = 80f * SCALE_FACTOR
        val BUTTON_RADIUS: Float get() = 60f * SCALE_FACTOR
        
        // Positioning (Percentages of screen - these stay constant)
        const val JOYSTICK_X_PCT = 0.15f
        const val JOYSTICK_Y_PCT = 0.35f
        const val BUTTON_X_PCT = 0.85f
        const val BUTTON_Y_PCT = 0.35f
    }

    object Game {
        const val INITIAL_ASTEROID_COUNT = 9
        const val LEVEL_UP_INPUT_DELAY = 0.75f
        const val WAVE_SPAWN_MULTIPLIER = 2 // + level
        
        // Intro animation durations
        const val INTRO_UI_FADE_DURATION = 0.5f
        const val INTRO_SLIDE_DURATION = 1.0f
        const val INTRO_TOTAL_DURATION = 1.5f
    }

    object Rendering {
        const val DEFAULT_ZOOM = 0.85f
    }
}
