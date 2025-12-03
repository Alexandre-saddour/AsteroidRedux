package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Constants

class MenuScreen(private val game: AsteroidsGame) : ScreenAdapter() {
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
        font.data.setScale(2.5f) // Title
        
        font.draw(game.batch, "ASTEROIDS REDUX", 0f, Constants.WORLD_HEIGHT / 2 + 50f, Constants.WORLD_WIDTH, Align.center, false)
        
        font.data.setScale(1.5f) // Subtitle
        font.draw(game.batch, "Tap to Play", 0f, Constants.WORLD_HEIGHT / 2 - 20f, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()

        if (Gdx.input.justTouched()) {
            game.screen = GameScreen(game)
            dispose()
        }
    }
}
