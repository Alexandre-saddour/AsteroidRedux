package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Button
import com.example.asteroidsredux.utils.ButtonRenderer
import com.example.asteroidsredux.utils.Constants

class GameOverScreen(game: AsteroidsGame, private val score: Int) : BaseScreen(game) {
    
    // Dynamic sizes (base values at 1280x720, scaled by SCALE_FACTOR)
    private val scaleFactor = Constants.UI.SCALE_FACTOR
    private val buttonWidth = 300f * scaleFactor
    private val buttonHeight = 60f * scaleFactor
    private val titleOffsetY = 80f * scaleFactor
    private val buttonOffsetY = 100f * scaleFactor
    private val titleTextScale = 2.5f * scaleFactor
    private val scoreTextScale = 1.8f * scaleFactor
    private val buttonTextScale = 1.5f * scaleFactor

    private val restartButton = Button(
        x = 0f,
        y = 0f,
        width = buttonWidth,
        height = buttonHeight,
        text = "Tap to Restart",
        fillColor = Color.DARK_GRAY.cpy().apply { a = 0.5f },
        borderColor = Color.WHITE,
        textColor = Color.WHITE,
        textScale = buttonTextScale
    )

    override fun drawUi(delta: Float) {
        // updateCamera() is called by BaseScreen

        val centerX = Constants.WORLD_WIDTH / 2
        val centerY = Constants.WORLD_HEIGHT / 2

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(titleTextScale)
        font.draw(game.batch, "GAME OVER", 0f, centerY + titleOffsetY, Constants.WORLD_WIDTH, Align.center, false)
        
        font.data.setScale(scoreTextScale)
        font.draw(game.batch, "Score: $score", 0f, centerY, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()

        // Update button position for resize
        val btn = restartButton.copy(
            x = centerX - buttonWidth / 2,
            y = centerY - buttonOffsetY
        )
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), btn)

        if (Gdx.input.justTouched()) {
            game.screen = GameScreen(game)
            dispose()
        }
    }
}
