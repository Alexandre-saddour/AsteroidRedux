package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.utils.Button
import com.example.asteroidsredux.utils.ButtonRenderer
import com.example.asteroidsredux.utils.Constants

class MenuScreen(game: AsteroidsGame) : BaseScreen(game) {

    // Use global scale factor from Constants
    private val scaleFactor = Constants.UI.SCALE_FACTOR

    // Dynamic button sizes (base values at 1280x720, scaled by SCALE_FACTOR)
    private val playButtonWidth = 300f * scaleFactor
    private val playButtonHeight = 80f * scaleFactor
    private val customizeButtonWidth = 250f * scaleFactor
    private val customizeButtonHeight = 60f * scaleFactor
    private val buttonSpacing = 30f * scaleFactor
    private val titleOffsetY = 150f * scaleFactor
    private val titleWidth = 600f * scaleFactor
    private val titleHeight = 150f * scaleFactor

    // Dynamic text scales
    private val playTextScale = 2.5f * scaleFactor
    private val customizeTextScale = 1.8f * scaleFactor

    // Assets
    private val titleLogo = game.assets.getTitleLogo()
    private val buttonDefault = game.assets.getButtonDefault()
    private val buttonPressed = game.assets.getButtonPressed()

    private val playButton = Button(
        x = 0f, // Will be recalculated in render
        y = 0f,
        width = playButtonWidth,
        height = playButtonHeight,
        text = "PLAY",
        textColor = Color.CYAN,
        textScale = playTextScale,
        texture = buttonDefault,
        pressedTexture = buttonPressed
    )

    private val customizeButton = Button(
        x = 0f,
        y = 0f,
        width = customizeButtonWidth,
        height = customizeButtonHeight,
        text = "Skin Selection",
        textColor = Color.WHITE,
        textScale = customizeTextScale,
        texture = buttonDefault,
        pressedTexture = buttonPressed
    )

    override fun drawUi(delta: Float) {
        // updateCamera() is called by BaseScreen before drawUi

        val centerX = Constants.WORLD_WIDTH / 2
        val centerY = Constants.WORLD_HEIGHT / 2

        // Update button positions and sizes dynamically
        val playBtn = playButton.copy(
            x = centerX - playButtonWidth / 2,
            y = centerY - playButtonHeight / 2
        )
        val customizeBtn = customizeButton.copy(
            x = centerX - customizeButtonWidth / 2,
            y = playBtn.y - customizeButtonHeight - buttonSpacing
        )

        // Check for pressed state
        val touchX = ButtonRenderer.getTouchX()
        val touchY = ButtonRenderer.getTouchY()
        val isTouching = Gdx.input.isTouched

        val isPlayPressed = isTouching && ButtonRenderer.isClicked(playBtn, touchX, touchY)
        val isCustomizePressed = isTouching && ButtonRenderer.isClicked(customizeBtn, touchX, touchY)

        // Draw buttons
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), playBtn, isPlayPressed)
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), customizeBtn, isCustomizePressed)

        // Draw title logo
        game.batch.begin()
        game.batch.draw(
            titleLogo,
            centerX - titleWidth / 2,
            centerY + titleOffsetY - titleHeight / 2,
            titleWidth,
            titleHeight
        )
        game.batch.end()

        // Only handle input if this is the active screen
        if (game.screen == this) {
            // Handle back button to exit app from main menu
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
                Gdx.app.exit()
                return
            }

            if (Gdx.input.justTouched()) {
                val touchX = ButtonRenderer.getTouchX()
                val touchY = ButtonRenderer.getTouchY()

                if (ButtonRenderer.isClicked(playBtn, touchX, touchY)) {
                    // Switch directly to GameScreen - it handles the entire intro animation
                    // including fading out menu UI and sliding in entities
                    game.screen = GameScreen(game)
                    dispose()
                    return
                }

                if (ButtonRenderer.isClicked(customizeBtn, touchX, touchY)) {
                    game.changeScreen(SkinSelectionScreen(game), TransitionType.MENU_TO_CUSTOMIZE)
                    // dispose() is handled by TransitionScreen/Game
                    return
                }
            }
        }
    }
}
