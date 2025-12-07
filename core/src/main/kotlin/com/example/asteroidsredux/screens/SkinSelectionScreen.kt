package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.skins.SkinCategory
import com.example.asteroidsredux.utils.Button
import com.example.asteroidsredux.utils.ButtonRenderer
import com.example.asteroidsredux.utils.Constants

class SkinSelectionScreen(game: AsteroidsGame) : BaseScreen(game) {
    private var selectedCategory = SkinCategory.SHIP

    // Dynamic scale factor
    private val scaleFactor = Constants.UI.SCALE_FACTOR

    // Assets
    private val buttonDefault = game.assets.getButtonDefault()
    private val buttonPressed = game.assets.getButtonPressed()
    private val uiCard = game.assets.getCard()
    private val uiCardSelected = game.assets.getCardSelected()

    // Layout constants (dynamic)
    private val tabHeight = 60f * scaleFactor
    private val tabWidth = 200f * scaleFactor
    private val skinCardWidth = Constants.UI.CARD_WIDTH * 0.8f // Slightly smaller than HUD cards
    private val skinCardHeight = Constants.UI.CARD_HEIGHT * 0.8f
    private val cardSpacing = 30f * scaleFactor
    private val cardsPerRow = 3

    // Button sizes (dynamic)
    private val backButtonWidth = 150f * scaleFactor
    private val backButtonHeight = 50f * scaleFactor

    // Text scales (dynamic)
    private val headerTextScale = 3f * scaleFactor
    private val tabTextScaleSelected = 1.6f * scaleFactor
    private val tabTextScaleNormal = 1.4f * scaleFactor
    private val skinNameTextScale = 1.0f * scaleFactor
    private val unlockConditionTextScale = 0.8f * scaleFactor
    private val buttonTextScale = 1.5f * scaleFactor

    private val backButton = Button(
        x = 30f * scaleFactor,
        y = Constants.WORLD_HEIGHT - 80f * scaleFactor, // Moved to top left
        width = backButtonWidth,
        height = backButtonHeight,
        text = "BACK",
        textColor = Color.CYAN,
        textScale = buttonTextScale,
        texture = buttonDefault,
        pressedTexture = buttonPressed
    )

    // Scroll state
    private var scrollY = 0f
    private var maxScrollY = 0f
    private var isDragging = false
    private var lastTouchY = 0f
    private val scrollSpeed = 1.5f // Multiplier for drag speed

    // Scissor rectangle for clipping
    private val scissors = Rectangle()

    // Shader animation state
    private var selectionTime = 0f
    private var lastSelectedSkinId: String? = null

    override fun drawUi(delta: Float) {
        // Handle back button (hardware)
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACK)) {
            game.changeScreen(MenuScreen(game), com.example.asteroidsredux.screens.TransitionType.CUSTOMIZE_TO_MENU)
            // dispose() is handled by TransitionScreen/Game
            return
        }

        // updateCamera() is called by BaseScreen

//        drawHeader()
        drawCategoryTabs()

        // Calculate grid area for clipping
        val gridStartY = Constants.WORLD_HEIGHT - 180f * scaleFactor

        // Prepare scissor test
        game.batch.flush()
        ScissorStack.calculateScissors(camera, game.batch.transformMatrix,
            Rectangle(0f, 0f, Constants.WORLD_WIDTH, gridStartY), scissors)

        if (ScissorStack.pushScissors(scissors)) {
            drawSkinGrid()
            game.batch.flush()
            ScissorStack.popScissors()
        }

        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), backButton)

        handleInput()
    }

    private fun drawHeader() {
        game.batch.begin()
        val font = game.assets.getFont()
        font.data.setScale(headerTextScale)

        // Glow effect
        font.color = Color.CYAN.cpy().apply { a = 0.3f }
        val title = "CUSTOMIZE"
        val titleY = Constants.WORLD_HEIGHT - 30f * scaleFactor
        font.draw(game.batch, title, 4f, titleY - 4f, Constants.WORLD_WIDTH, Align.center, false)
        font.draw(game.batch, title, -4f, titleY + 4f, Constants.WORLD_WIDTH, Align.center, false)

        font.color = Color.CYAN
        font.draw(game.batch, title, 0f, titleY, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()
    }

    private fun drawCategoryTabs() {
        val categories = SkinCategory.values()
        val totalTabWidth = categories.size * tabWidth + (categories.size - 1) * 20f * scaleFactor
        val startX = (Constants.WORLD_WIDTH - totalTabWidth) / 2f
        val y = Constants.WORLD_HEIGHT - 130f * scaleFactor

        for ((i, category) in categories.withIndex()) {
            val x = startX + i * (tabWidth + 20f * scaleFactor)
            val isSelected = category == selectedCategory

            // Create a temporary button for rendering
            val tabBtn = Button(
                x = x,
                y = y,
                width = tabWidth,
                height = tabHeight,
                text = category.name,
                textColor = if (isSelected) Color.CYAN else Color.GRAY,
                textScale = if (isSelected) tabTextScaleSelected else tabTextScaleNormal,
                texture = if (isSelected) buttonPressed else buttonDefault,
                pressedTexture = buttonPressed
            )

            // We use ButtonRenderer but force the "pressed" texture if selected
            // Actually ButtonRenderer logic relies on "isPressed" param for texture swap.
            // Let's just manually draw it or use ButtonRenderer with a trick.
            // Better to just draw manually here for full control or update ButtonRenderer.
            // Let's draw manually using batch since we have textures.

            game.batch.begin()
            val texture = if (isSelected) buttonPressed else buttonDefault
            game.batch.draw(texture, x, y, tabWidth, tabHeight)

            val font = game.assets.getFont()
            font.data.setScale(tabBtn.textScale)
            font.color = tabBtn.textColor

            val layout = com.badlogic.gdx.graphics.g2d.GlyphLayout(font, category.name)
            val textX = x + (tabWidth - layout.width) / 2
            val textY = y + (tabHeight + layout.height) / 2
            font.draw(game.batch, category.name, textX, textY)
            game.batch.end()
        }
    }

    private fun drawSkinGrid() {
        val skins = game.skinManager.getSkinsForCategory(selectedCategory)
        val selectedSkinId = game.skinManager.getSelectedSkinId(selectedCategory)

        val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val startY = Constants.WORLD_HEIGHT - 180f * scaleFactor + scrollY // Apply scrollY

        // Calculate max scroll
        val rows = (skins.size + cardsPerRow - 1) / cardsPerRow
        val totalGridHeight = rows * (skinCardHeight + cardSpacing)
        val visibleHeight = Constants.WORLD_HEIGHT - 180f * scaleFactor
        maxScrollY = (totalGridHeight - visibleHeight + 100f * scaleFactor).coerceAtLeast(0f)

        game.batch.begin()
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            // Culling: Don't draw if out of screen
            if (y + skinCardHeight < 0 || y > Constants.WORLD_HEIGHT) continue

            val isSelected = skin.id == selectedSkinId
            val isUnlocked = game.skinManager.isUnlocked(selectedCategory, skin.id)

            // Draw Card Background
            // Always draw unselected card as base
            game.batch.draw(uiCard, x, y, skinCardWidth, skinCardHeight)
            
            if (isSelected) {
                // Check if selection changed
                if (skin.id != lastSelectedSkinId) {
                    lastSelectedSkinId = skin.id
                    selectionTime = 0f
                }
                
                // Update selection time
                selectionTime += Gdx.graphics.deltaTime
                val activationDuration = 0.5f
                val activation = (selectionTime / activationDuration).coerceIn(0f, 1f)
                
                val shader = game.assets.shaderManager.get("cold")
                game.batch.shader = shader
                shader.setUniformf("u_time", com.example.asteroidsredux.AsteroidsGame.stateTime)
                shader.setUniformf("u_lightningColor", 0f, 1f, 1f, 1f) // Cyan
                shader.setUniformf("u_regionBounds", uiCardSelected.u, uiCardSelected.v, uiCardSelected.u2, uiCardSelected.v2)
                shader.setUniformf("u_activation", activation)
                
                // Fade in the selected card on top
                game.batch.setColor(1f, 1f, 1f, activation)
                game.batch.draw(uiCardSelected, x, y, skinCardWidth, skinCardHeight)
                game.batch.setColor(Color.WHITE)
                game.batch.shader = null
            }

            // Draw Skin Preview
            val textureRegion = game.assets.getTextureRegion(skin)
            if (textureRegion != null) {
                val previewSize = 100f * scaleFactor
                val previewX = x + (skinCardWidth - previewSize) / 2f
                val previewY = y + (skinCardHeight - previewSize) / 2f + 20f * scaleFactor

                val color = if (isUnlocked) Color.WHITE else Color.GRAY
                game.batch.setColor(color)
                game.batch.draw(textureRegion, previewX, previewY, previewSize, previewSize)
                game.batch.setColor(Color.WHITE)
            }

            // Draw Text
            val font = game.assets.getFont()
            font.data.setScale(skinNameTextScale)
            font.color = if (isSelected) Color.CYAN else Color.WHITE
            font.draw(game.batch, skin.displayName, x, y + 50f * scaleFactor, skinCardWidth, Align.center, false)

            if (!isUnlocked && skin.unlockCondition != null) {
                font.data.setScale(unlockConditionTextScale)
                font.color = Color.GRAY
                font.draw(game.batch, "🔒 ${skin.unlockCondition}", x, y + 25f * scaleFactor, skinCardWidth, Align.center, true)
            }
        }
        game.batch.end()
    }

    private var wasTouched = false

    private fun handleInput() {
        // Only handle input if this is the active screen
        if (game.screen != this) return

        val touchX = ButtonRenderer.getTouchX()
        val touchY = ButtonRenderer.getTouchY()
        val isTouched = Gdx.input.isTouched

        if (isTouched) {
            if (!wasTouched) {
                // Touch just started
                isDragging = false
                lastTouchY = touchY

                // Check back button (always visible)
                if (ButtonRenderer.isClicked(backButton, touchX, touchY)) {
                    game.changeScreen(MenuScreen(game), com.example.asteroidsredux.screens.TransitionType.CUSTOMIZE_TO_MENU)
                    return
                }

                // Check category tabs (always visible)
                val categories = SkinCategory.values()
                val totalTabWidth = categories.size * tabWidth + (categories.size - 1) * 20f * scaleFactor
                val startX = (Constants.WORLD_WIDTH - totalTabWidth) / 2f
                val tabY = Constants.WORLD_HEIGHT - 130f * scaleFactor

                if (touchY >= tabY && touchY <= tabY + tabHeight) {
                    for ((i, category) in categories.withIndex()) {
                        val x = startX + i * (tabWidth + 20f * scaleFactor)
                        if (touchX >= x && touchX <= x + tabWidth) {
                            selectedCategory = category
                            scrollY = 0f // Reset scroll on tab change
                            wasTouched = true // Mark as touched so we don't re-trigger
                            return
                        }
                    }
                }
            } else {
                // Continuing touch (Drag)
                val deltaY = touchY - lastTouchY
                lastTouchY = touchY

                // If dragging in the grid area
                if (touchY < Constants.WORLD_HEIGHT - 180f * scaleFactor) {
                    if (kotlin.math.abs(deltaY) > 2f) {
                        isDragging = true
                    }

                    if (isDragging) {
                        scrollY += deltaY * scrollSpeed
                        scrollY = scrollY.coerceIn(0f, maxScrollY)
                    }
                }
            }
        } else {
            // Touch released
            if (wasTouched) {
                if (!isDragging && touchY < Constants.WORLD_HEIGHT - 180f * scaleFactor) {
                    handleGridClick(touchX, touchY)
                }
                isDragging = false
            }
        }

        wasTouched = isTouched
    }

    private fun handleGridClick(touchX: Float, touchY: Float) {
        val skins = game.skinManager.getSkinsForCategory(selectedCategory)
        val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val startY = Constants.WORLD_HEIGHT - 180f * scaleFactor + scrollY

        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            if (touchX >= x && touchX <= x + skinCardWidth && touchY >= y && touchY <= y + skinCardHeight) {
                if (game.skinManager.isUnlocked(selectedCategory, skin.id)) {
                    game.skinManager.selectSkin(selectedCategory, skin.id)
                }
                return
            }
        }
    }
}
