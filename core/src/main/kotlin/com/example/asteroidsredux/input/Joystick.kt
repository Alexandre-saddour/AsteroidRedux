package com.example.asteroidsredux.input

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import kotlin.math.atan2

class Joystick(
    private val defaultPosition: Vector2,
    val baseRadius: Float,
    val knobRadius: Float = baseRadius * 0.4f,
    val color: Color = Color.CYAN
) {
    private val currentBasePosition = Vector2(defaultPosition)
    private val knobPosition = Vector2(defaultPosition)
    private var isActive = false
    private var activePointer = -1
    
    // Returns rotation value from -1 (left) to 1 (right), 0 when centered
    var rotationValue = 0f
        private set
    
    // Position to render at (default when inactive, touch position when active)
    val basePosition: Vector2
        get() = currentBasePosition
    
    // Returns the angle in radians from the center, or null if not active
    val angle: Float?
        get() = if (isActive) {
            val dx = knobPosition.x - currentBasePosition.x
            val dy = knobPosition.y - currentBasePosition.y
            if (dx != 0f || dy != 0f) atan2(dy, dx) else null
        } else null
    
    fun handleTouchDown(x: Float, y: Float, pointer: Int): Boolean {
        // Floating joystick - always accept the touch and move base to touch position
        isActive = true
        activePointer = pointer
        currentBasePosition.set(x, y)
        knobPosition.set(x, y)
        rotationValue = 0f
        return true
    }
    
    fun handleTouchDragged(x: Float, y: Float, pointer: Int): Boolean {
        if (isActive && pointer == activePointer) {
            updateKnobPosition(x, y)
            return true
        }
        return false
    }
    
    fun handleTouchUp(pointer: Int): Boolean {
        if (pointer == activePointer) {
            isActive = false
            activePointer = -1
            currentBasePosition.set(defaultPosition)
            knobPosition.set(defaultPosition)
            rotationValue = 0f
            return true
        }
        return false
    }
    
    private fun updateKnobPosition(x: Float, y: Float) {
        val offset = Vector2(x - currentBasePosition.x, y - currentBasePosition.y)
        val distance = offset.len()
        
        // Clamp knob within the base radius
        val maxDistance = baseRadius - knobRadius
        if (distance > maxDistance) {
            offset.setLength(maxDistance)
        }
        
        knobPosition.set(currentBasePosition.x + offset.x, currentBasePosition.y + offset.y)
        
        // Calculate rotation value based on horizontal position
        // -1 = full left, 0 = center, 1 = full right
        rotationValue = if (maxDistance > 0) {
            (offset.x / maxDistance).coerceIn(-1f, 1f)
        } else {
            0f
        }
    }
    
    fun render(shapeRenderer: ShapeRenderer) {
        // Draw base circle (faded when inactive, visible when active)
        shapeRenderer.color = color.cpy().apply { a = if (isActive) 0.6f else 0.3f }
        shapeRenderer.circle(currentBasePosition.x, currentBasePosition.y, baseRadius, 40)
        
        // Draw knob
        shapeRenderer.color = color.cpy().apply { a = if (isActive) 0.9f else 0.4f }
        shapeRenderer.circle(knobPosition.x, knobPosition.y, knobRadius, 30)
    }
}
