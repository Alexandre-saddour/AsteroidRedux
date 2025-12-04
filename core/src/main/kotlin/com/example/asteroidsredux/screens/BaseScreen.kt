package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Constants

/**
 * Base class for all game screens.
 * Handles common camera setup and resize logic.
 */
abstract class BaseScreen(protected val game: AsteroidsGame) : ScreenAdapter() {
    protected val camera = OrthographicCamera()

    init {
        updateWorldDimensions(Gdx.graphics.width, Gdx.graphics.height)
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun resize(width: Int, height: Int) {
        updateWorldDimensions(width, height)
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    private fun updateWorldDimensions(width: Int, height: Int) {
        Constants.WORLD_WIDTH = width.toFloat()
        Constants.WORLD_HEIGHT = height.toFloat()
    }

    protected fun updateCamera() {
        camera.update()
        game.batch.projectionMatrix = camera.combined
        game.shapeRenderer.projectionMatrix = camera.combined
    }
}
