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

    private val restartButton = Button(
        x = Constants.WORLD_WIDTH / 2 - 150f,
        y = Constants.WORLD_HEIGHT / 2 - 100f,
        width = 300f,
        height = 60f,
        text = "Tap to Restart",
        fillColor = Color.DARK_GRAY.cpy().apply { a = 0.5f },
        borderColor = Color.WHITE,
        textColor = Color.WHITE,
        textScale = 1.5f
    )

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        updateCamera()

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(2.5f)
        font.draw(game.batch, "GAME OVER", 0f, Constants.WORLD_HEIGHT / 2 + 80f, Constants.WORLD_WIDTH, Align.center, false)
        
        font.data.setScale(1.8f)
        font.draw(game.batch, "Score: $score", 0f, Constants.WORLD_HEIGHT / 2, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()

        // Update button position for resize
        val btn = restartButton.copy(
            x = Constants.WORLD_WIDTH / 2 - restartButton.width / 2,
            y = Constants.WORLD_HEIGHT / 2 - 100f
        )
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), btn)

        if (Gdx.input.justTouched()) {
            game.screen = GameScreen(game)
            dispose()
        }
    }
}
