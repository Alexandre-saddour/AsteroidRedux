package com.example.asteroidsredux.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align

/**
 * Represents a clickable UI button.
 */
data class Button(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val text: String,
    val fillColor: Color = Color.DARK_GRAY,
    val borderColor: Color = Color.WHITE,
    val textColor: Color = Color.WHITE,
    val textScale: Float = 1.5f
)

/**
 * Utility for drawing and hit-testing UI buttons.
 */
object ButtonRenderer {
    
    /**
     * Draw a button with filled background, border, and centered text.
     */
    fun draw(
        shapeRenderer: ShapeRenderer,
        batch: SpriteBatch,
        font: BitmapFont,
        button: Button
    ) {
        // Draw filled background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = button.fillColor
        shapeRenderer.rect(button.x, button.y, button.width, button.height)
        shapeRenderer.end()
        
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = button.borderColor
        shapeRenderer.rect(button.x, button.y, button.width, button.height)
        shapeRenderer.end()
        
        // Draw text
        batch.begin()
        font.data.setScale(button.textScale)
        font.color = button.textColor
        font.draw(
            batch,
            button.text,
            button.x,
            button.y + button.height / 2 + font.lineHeight / 3,
            button.width,
            Align.center,
            false
        )
        batch.end()
    }
    
    /**
     * Check if touch coordinates are inside the button bounds.
     * Note: touchY should already be converted to world coordinates (Y-flipped).
     */
    fun isClicked(button: Button, touchX: Float, touchY: Float): Boolean {
        return touchX >= button.x && touchX <= button.x + button.width &&
               touchY >= button.y && touchY <= button.y + button.height
    }
    
    /**
     * Get touch coordinates in world space (Y-flipped).
     */
    fun getTouchY(): Float = Constants.WORLD_HEIGHT - Gdx.input.y.toFloat()
    fun getTouchX(): Float = Gdx.input.x.toFloat()
}
