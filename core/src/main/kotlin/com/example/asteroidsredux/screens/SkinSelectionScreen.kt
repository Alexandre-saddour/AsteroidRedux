package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
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
    
    // Layout constants (dynamic)
    private val tabHeight = 80f * scaleFactor
    private val tabWidth = 200f * scaleFactor
    private val skinCardWidth = 200f * scaleFactor
    private val skinCardHeight = 250f * scaleFactor
    private val cardSpacing = 30f * scaleFactor
    private val cardsPerRow = 3
    
    // Button sizes (dynamic)
    private val backButtonWidth = 120f * scaleFactor
    private val backButtonHeight = 40f * scaleFactor
    
    // Text scales (dynamic)
    private val headerTextScale = 3f * scaleFactor
    private val tabTextScaleSelected = 1.8f * scaleFactor
    private val tabTextScaleNormal = 1.5f * scaleFactor
    private val skinNameTextScale = 1.2f * scaleFactor
    private val unlockConditionTextScale = 0.9f * scaleFactor
    private val buttonTextScale = 1.5f * scaleFactor

    private val backButton = Button(
        x = 30f * scaleFactor,
        y = 30f * scaleFactor,
        width = backButtonWidth,
        height = backButtonHeight,
        text = "BACK",
        fillColor = Color(0.2f, 0.2f, 0.3f, 1f),
        borderColor = Color.GRAY,
        textColor = Color.WHITE,
        textScale = buttonTextScale
    )

    override fun render(delta: Float) {
        // Gradient background
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.rect(
            0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT,
            Color.valueOf("0a0a1a"), Color.valueOf("0a0a1a"),
            Color.valueOf("1a1a3a"), Color.valueOf("1a1a3a")
        )
        game.shapeRenderer.end()

        updateCamera()
        
        drawHeader()
        drawCategoryTabs()
        drawSkinGrid()
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), backButton)
        
        handleInput()
    }

    private fun drawHeader() {
        game.batch.begin()
        val font = game.assets.getFont()
        font.data.setScale(headerTextScale)
        font.color = Color.CYAN
        font.draw(game.batch, "CUSTOMIZE", 0f, Constants.WORLD_HEIGHT - 30f * scaleFactor, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()
    }

    private fun drawCategoryTabs() {
        val categories = SkinCategory.values()
        val startX = (Constants.WORLD_WIDTH - (categories.size * tabWidth)) / 2f
        val y = Constants.WORLD_HEIGHT - 120f * scaleFactor

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color.DARK_GRAY
        game.shapeRenderer.rect(startX, y, categories.size * tabWidth, 2f * scaleFactor)
        
        val selectedIndex = categories.indexOf(selectedCategory)
        game.shapeRenderer.color = Color.CYAN
        game.shapeRenderer.rect(startX + selectedIndex * tabWidth, y, tabWidth, 4f * scaleFactor)
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        for ((i, category) in categories.withIndex()) {
            val x = startX + i * tabWidth
            val isSelected = category == selectedCategory
            
            font.data.setScale(if (isSelected) tabTextScaleSelected else tabTextScaleNormal)
            font.color = if (isSelected) Color.WHITE else Color.GRAY
            font.draw(game.batch, category.name, x, y + 40f * scaleFactor, tabWidth, Align.center, false)
        }
        game.batch.end()
    }

    private fun drawSkinGrid() {
        val skins = game.skinManager.getSkinsForCategory(selectedCategory)
        val selectedSkinId = game.skinManager.getSelectedSkinId(selectedCategory)
        
        val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val startY = Constants.WORLD_HEIGHT - 180f * scaleFactor

        // First pass: Draw all card backgrounds
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight
            val isSelected = skin.id == selectedSkinId

            if (isSelected) {
                game.shapeRenderer.color = Color.CYAN.cpy().apply { a = 0.2f }
                game.shapeRenderer.rect(x - 5f * scaleFactor, y - 5f * scaleFactor, skinCardWidth + 10f * scaleFactor, skinCardHeight + 10f * scaleFactor)
            }
            
            game.shapeRenderer.color = Color(0.15f, 0.15f, 0.25f, 0.9f)
            game.shapeRenderer.rect(x, y, skinCardWidth, skinCardHeight)
        }
        game.shapeRenderer.end()

        // Second pass: Draw selection borders
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight
            val isSelected = skin.id == selectedSkinId

            if (isSelected) {
                game.shapeRenderer.color = Color.CYAN
                game.shapeRenderer.rect(x, y, skinCardWidth, skinCardHeight)
            }
        }
        game.shapeRenderer.end()

        // Third pass: Draw textures and text
        game.batch.begin()
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            val isUnlocked = game.skinManager.isUnlocked(selectedCategory, skin.id)
            val isSelected = skin.id == selectedSkinId

            val textureRegion = game.assets.getTextureRegion(skin)
            if (textureRegion != null) {
                val previewSize = 120f * scaleFactor
                val previewX = x + (skinCardWidth - previewSize) / 2f
                val previewY = y + (skinCardHeight - previewSize) / 2f + 20f * scaleFactor
                
                val color = if (isUnlocked) Color.WHITE else Color.GRAY
                game.batch.setColor(color)
                game.batch.draw(textureRegion, previewX, previewY, previewSize, previewSize)
                game.batch.setColor(Color.WHITE)
            }

            val font = game.assets.getFont()
            font.data.setScale(skinNameTextScale)
            font.color = if (isSelected) Color.CYAN else Color.WHITE
            font.draw(game.batch, skin.displayName, x, y + 40f * scaleFactor, skinCardWidth, Align.center, false)
            
            if (!isUnlocked && skin.unlockCondition != null) {
                font.data.setScale(unlockConditionTextScale)
                font.color = Color.GRAY
                font.draw(game.batch, "🔒 ${skin.unlockCondition}", x, y + 20f * scaleFactor, skinCardWidth, Align.center, true)
            }
        }
        game.batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = ButtonRenderer.getTouchX()
            val touchY = ButtonRenderer.getTouchY()

            // Check back button
            if (ButtonRenderer.isClicked(backButton, touchX, touchY)) {
                game.screen = MenuScreen(game)
                dispose()
                return
            }

            // Check category tabs
            val categories = SkinCategory.values()
            val startX = (Constants.WORLD_WIDTH - (categories.size * tabWidth)) / 2f
            val tabY = Constants.WORLD_HEIGHT - 120f * scaleFactor
            
            if (touchY > tabY && touchY < tabY + 60f * scaleFactor) {
                val relativeX = touchX - startX
                if (relativeX >= 0 && relativeX < categories.size * tabWidth) {
                    val tabIndex = (relativeX / tabWidth).toInt()
                    selectedCategory = categories[tabIndex]
                    return
                }
            }

            // Check skin cards
            val skins = game.skinManager.getSkinsForCategory(selectedCategory)
            val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
            val gridStartX = (Constants.WORLD_WIDTH - totalWidth) / 2f
            val gridStartY = Constants.WORLD_HEIGHT - 180f * scaleFactor

            for ((i, skin) in skins.withIndex()) {
                val row = i / cardsPerRow
                val col = i % cardsPerRow
                val x = gridStartX + col * (skinCardWidth + cardSpacing)
                val y = gridStartY - row * (skinCardHeight + cardSpacing) - skinCardHeight

                if (touchX >= x && touchX <= x + skinCardWidth && touchY >= y && touchY <= y + skinCardHeight) {
                    if (game.skinManager.isUnlocked(selectedCategory, skin.id)) {
                        game.skinManager.selectSkin(selectedCategory, skin.id)
                    }
                    return
                }
            }
        }
    }
}
