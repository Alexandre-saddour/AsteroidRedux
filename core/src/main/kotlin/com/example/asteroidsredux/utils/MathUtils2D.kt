package com.example.asteroidsredux.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

object MathUtils2D {
    fun wrapAround(position: Vector2, width: Float, height: Float) {
        if (position.x < 0) position.x = width
        if (position.x > width) position.x = 0f
        if (position.y < 0) position.y = height
        if (position.y > height) position.y = 0f
    }

    fun randomVector(length: Float): Vector2 {
        val angle = MathUtils.random(0f, MathUtils.PI2)
        return Vector2(MathUtils.cos(angle) * length, MathUtils.sin(angle) * length)
    }
}
