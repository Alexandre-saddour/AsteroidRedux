package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Constants

class MenuScreen(private val game: AsteroidsGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    
    private val playBtnWidth = 300f
    private val playBtnHeight = 80f
    private val customizeBtnWidth = 250f
    private val customizeBtnHeight = 60f

    init {
        Constants.WORLD_WIDTH = Gdx.graphics.width.toFloat()
        Constants.WORLD_HEIGHT = Gdx.graphics.height.toFloat()
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun resize(width: Int, height: Int) {
        Constants.WORLD_WIDTH = width.toFloat()
        Constants.WORLD_HEIGHT = height.toFloat()
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined
        game.shapeRenderer.projectionMatrix = camera.combined

        val centerX = Constants.WORLD_WIDTH / 2
        val centerY = Constants.WORLD_HEIGHT / 2

        // Draw Play button
        val playBtnX = centerX - playBtnWidth / 2
        val playBtnY = centerY - 20f
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color.DARK_GRAY
        game.shapeRenderer.rect(playBtnX, playBtnY, playBtnWidth, playBtnHeight)
        game.shapeRenderer.end()
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = Color.CYAN
        game.shapeRenderer.rect(playBtnX, playBtnY, playBtnWidth, playBtnHeight)
        game.shapeRenderer.end()

        // Draw Customize button
        val customizeBtnX = centerX - customizeBtnWidth / 2
        val customizeBtnY = playBtnY - customizeBtnHeight - 30f
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color.DARK_GRAY.cpy().apply { a = 0.7f }
        game.shapeRenderer.rect(customizeBtnX, customizeBtnY, customizeBtnWidth, customizeBtnHeight)
        game.shapeRenderer.end()
        
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.rect(customizeBtnX, customizeBtnY, customizeBtnWidth, customizeBtnHeight)
        game.shapeRenderer.end()

        // Draw text
        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(3f)
        font.draw(game.batch, "ASTEROIDS REDUX", 0f, centerY + 150f, Constants.WORLD_WIDTH, Align.center, false)
        
        font.data.setScale(2.5f)
        font.color = Color.CYAN
        font.draw(game.batch, "PLAY", playBtnX, playBtnY + playBtnHeight / 2 + 15f, playBtnWidth, Align.center, false)
        
        font.data.setScale(1.8f)
        font.color = Color.WHITE
        font.draw(game.batch, "Customize", customizeBtnX, customizeBtnY + customizeBtnHeight / 2 + 10f, customizeBtnWidth, Align.center, false)
        game.batch.end()

        handleInput(playBtnX, playBtnY, customizeBtnX, customizeBtnY)
    }

    private fun handleInput(playBtnX: Float, playBtnY: Float, customizeBtnX: Float, customizeBtnY: Float) {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Constants.WORLD_HEIGHT - Gdx.input.y.toFloat()

            // Check Play button
            if (touchX >= playBtnX && touchX <= playBtnX + playBtnWidth &&
                touchY >= playBtnY && touchY <= playBtnY + playBtnHeight) {
                game.screen = GameScreen(game)
                dispose()
                return
            }

            // Check Customize button
            if (touchX >= customizeBtnX && touchX <= customizeBtnX + customizeBtnWidth &&
                touchY >= customizeBtnY && touchY <= customizeBtnY + customizeBtnHeight) {
                game.screen = SkinSelectionScreen(game)
                dispose()
                return
            }
        }
    }
}
