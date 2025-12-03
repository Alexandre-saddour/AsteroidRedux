package com.example.asteroidsredux.input

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2

class TouchButton(
    val position: Vector2,
    val radius: Float,
    val color: Color = Color.CYAN,
    val label: String = ""
) {
    private val circle = Circle(position.x, position.y, radius)
    var isPressed = false
    
    fun contains(x: Float, y: Float): Boolean {
        return circle.contains(x, y)
    }
    
    fun render(shapeRenderer: ShapeRenderer) {
        // Outer ring
        shapeRenderer.color = color.cpy().apply { a = 0.5f }
        shapeRenderer.circle(position.x, position.y, radius, 40)
        
        // Inner filled circle when pressed
        if (isPressed) {
            shapeRenderer.color = color.cpy().apply { a = 0.3f }
            // Draw filled circle by using filled shape type
        }
    }
}
