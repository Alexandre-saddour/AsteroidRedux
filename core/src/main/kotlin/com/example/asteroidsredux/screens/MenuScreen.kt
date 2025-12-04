package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Button
import com.example.asteroidsredux.utils.ButtonRenderer
import com.example.asteroidsredux.utils.Constants

class MenuScreen(game: AsteroidsGame) : BaseScreen(game) {
    
    private val playButton = Button(
        x = Constants.WORLD_WIDTH / 2 - 150f,
        y = Constants.WORLD_HEIGHT / 2 - 20f,
        width = 300f,
        height = 80f,
        text = "PLAY",
        fillColor = Color.DARK_GRAY,
        borderColor = Color.CYAN,
        textColor = Color.CYAN,
        textScale = 2.5f
    )
    
    private val customizeButton = Button(
        x = Constants.WORLD_WIDTH / 2 - 125f,
        y = Constants.WORLD_HEIGHT / 2 - 130f,
        width = 250f,
        height = 60f,
        text = "Customize",
        fillColor = Color.DARK_GRAY.cpy().apply { a = 0.7f },
        borderColor = Color.WHITE,
        textColor = Color.WHITE,
        textScale = 1.8f
    )

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        updateCamera()

        val centerX = Constants.WORLD_WIDTH / 2
        val centerY = Constants.WORLD_HEIGHT / 2

        // Update button positions (in case of resize)
        val playBtn = playButton.copy(
            x = centerX - playButton.width / 2,
            y = centerY - 20f
        )
        val customizeBtn = customizeButton.copy(
            x = centerX - customizeButton.width / 2,
            y = playBtn.y - customizeButton.height - 30f
        )

        // Draw buttons
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), playBtn)
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), customizeBtn)

        // Draw title
        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(3f)
        font.draw(game.batch, "ASTEROIDS REDUX", 0f, centerY + 150f, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()

        handleInput(playBtn, customizeBtn)
    }

    private fun handleInput(playBtn: Button, customizeBtn: Button) {
        if (Gdx.input.justTouched()) {
            val touchX = ButtonRenderer.getTouchX()
            val touchY = ButtonRenderer.getTouchY()

            if (ButtonRenderer.isClicked(playBtn, touchX, touchY)) {
                game.screen = GameScreen(game)
                dispose()
                return
            }

            if (ButtonRenderer.isClicked(customizeBtn, touchX, touchY)) {
                game.screen = SkinSelectionScreen(game)
                dispose()
                return
            }
        }
    }
}
