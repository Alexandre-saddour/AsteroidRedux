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
        Constants.WORLD_WIDTH = width.toFloat()//  * 2f
        Constants.WORLD_HEIGHT = height.toFloat()// * 2f
    }

    abstract fun drawUi(delta: Float)

    override fun render(delta: Float) {
        // Standard render: Clear screen, draw background, draw UI
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT)

        updateCamera()
        
        // Update and draw background
        game.backgroundRenderer.update(delta)
        game.backgroundRenderer.draw(game.batch)

        drawUi(delta)
    }

    fun renderUiOnly(delta: Float) {
        // Draw UI only (no clear, no background)
        // Assumes camera is already updated or will be updated inside drawUi if needed
        updateCamera()
        drawUi(delta)
    }

    protected fun updateCamera() {
        camera.update()
        game.batch.projectionMatrix = camera.combined
        game.shapeRenderer.projectionMatrix = camera.combined
    }
}
