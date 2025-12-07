package com.example.asteroidsredux.utils

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.example.asteroidsredux.AsteroidsGame

class BackgroundRenderer(private val game: AsteroidsGame) {
    private val bgStars: TextureRegion = game.assets.getBackgroundStars()
    private var bgScrollX = 0f
    private val bgScrollSpeed = 30f

    fun update(delta: Float) {
        bgScrollX += bgScrollSpeed * delta
        if (bgScrollX > bgStars.regionWidth) bgScrollX = 0f
    }

    fun draw(batch: SpriteBatch) {
        batch.begin()
        // Draw scrolling background (tiled)
        val bgWidth = bgStars.regionWidth.toFloat()
        val bgHeight = bgStars.regionHeight.toFloat()
        val screenWidth = Constants.WORLD_WIDTH
        val screenHeight = Constants.WORLD_HEIGHT

        var x = -bgScrollX
        while (x < screenWidth) {
            var y = 0f
            while (y < screenHeight) {
                batch.draw(bgStars, x, y)
                y += bgHeight
            }
            x += bgWidth
        }
        batch.end()
    }
}
