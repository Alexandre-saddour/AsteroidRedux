package com.example.asteroidsredux.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Utility for drawing rotated sprites centered on a position.
 */
object SpriteRenderer {

    /**
     * Draw a texture centered at (x, y), rotated by the given angle.
     * @param batch SpriteBatch to draw with
     * @param texture Texture to draw
     * @param x Center X position
     * @param y Center Y position
     * @param size Half-width/height of the sprite (total size is size * 2)
     * @param rotationDegrees Rotation in degrees
     */
    fun drawCentered(
        batch: SpriteBatch,
        texture: Texture,
        x: Float,
        y: Float,
        size: Float,
        rotationDegrees: Float
    ) {
        batch.draw(
            texture,
            x - size,          // x position (top-left corner after centering)
            y - size,          // y position
            size,              // originX (center of rotation)
            size,              // originY
            size * 2,          // width
            size * 2,          // height
            1f,                // scaleX
            1f,                // scaleY
            rotationDegrees,   // rotation
            0,                 // srcX
            0,                 // srcY
            texture.width,     // srcWidth
            texture.height,    // srcHeight
            false,             // flipX
            false              // flipY
        )

    }

    /**
     * Draw a texture region centered at (x, y), rotated by the given angle.
     */
    fun drawCentered(
        batch: SpriteBatch,
        region: TextureRegion,
        x: Float,
        y: Float,
        size: Float,
        rotationDegrees: Float
    ) {
        batch.draw(
            region,
            x - size,          // x position
            y - size,          // y position
            size,              // originX
            size,              // originY
            size * 2,          // width
            size * 2,          // height
            1f,                // scaleX
            1f,                // scaleY
            rotationDegrees    // rotation
        )
    }
}
