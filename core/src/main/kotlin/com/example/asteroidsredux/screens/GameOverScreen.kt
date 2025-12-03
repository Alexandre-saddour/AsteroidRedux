package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Constants

class GameOverScreen(private val game: AsteroidsGame, private val score: Int) : ScreenAdapter() {
    private val camera = OrthographicCamera()

    init {
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(0.1f)

        font.draw(game.batch, "GAME OVER", 0f, Constants.WORLD_HEIGHT / 2 + 10f, Constants.WORLD_WIDTH, Align.center, false)
        font.draw(game.batch, "Score: $score", 0f, Constants.WORLD_HEIGHT / 2, Constants.WORLD_WIDTH, Align.center, false)
        font.draw(game.batch, "Tap to Restart", 0f, Constants.WORLD_HEIGHT / 2 - 10f, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()

        if (Gdx.input.justTouched()) {
            game.screen = GameScreen(game)
            dispose()
        }
    }
}
