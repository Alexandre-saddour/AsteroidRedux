package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.skins.Skin
import com.example.asteroidsredux.skins.SkinCategory
import com.example.asteroidsredux.utils.Constants

class SkinSelectionScreen(private val game: AsteroidsGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private var selectedCategory = SkinCategory.SHIP
    
    // Layout constants
    private val tabHeight = 80f
    private val skinCardWidth = 200f
    private val skinCardHeight = 250f
    private val cardSpacing = 30f
    private val cardsPerRow = 3

    init {
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun resize(width: Int, height: Int) {
        Constants.WORLD_WIDTH = width.toFloat()
        Constants.WORLD_HEIGHT = height.toFloat()
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun render(delta: Float) {
        // Gradient background
        game.shapeRenderer.projectionMatrix = camera.combined
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.rect(
            0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT,
            Color.valueOf("0a0a1a"), Color.valueOf("0a0a1a"), // Bottom colors
            Color.valueOf("1a1a3a"), Color.valueOf("1a1a3a")  // Top colors
        )
        game.shapeRenderer.end()

        camera.update()
        game.batch.projectionMatrix = camera.combined
        
        drawHeader()
        drawCategoryTabs()
        drawSkinGrid()
        drawBackButton()
        
        handleInput()
    }

    private fun drawHeader() {
        game.batch.begin()
        val font = game.assets.getFont()
        font.data.setScale(3f)
        font.color = Color.CYAN
        font.draw(game.batch, "CUSTOMIZE", 0f, Constants.WORLD_HEIGHT - 30f, Constants.WORLD_WIDTH, Align.center, false)
        game.batch.end()
    }

    private fun drawCategoryTabs() {
        val categories = SkinCategory.values()
        val tabWidth = 200f
        val startX = (Constants.WORLD_WIDTH - (categories.size * tabWidth)) / 2f
        val y = Constants.WORLD_HEIGHT - 120f

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        // Tab background line
        game.shapeRenderer.color = Color.DARK_GRAY
        game.shapeRenderer.rect(startX, y, categories.size * tabWidth, 2f)
        
        // Selected tab indicator
        val selectedIndex = categories.indexOf(selectedCategory)
        game.shapeRenderer.color = Color.CYAN
        game.shapeRenderer.rect(startX + selectedIndex * tabWidth, y, tabWidth, 4f)
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        for ((i, category) in categories.withIndex()) {
            val x = startX + i * tabWidth
            val isSelected = category == selectedCategory
            
            font.data.setScale(if (isSelected) 1.8f else 1.5f)
            font.color = if (isSelected) Color.WHITE else Color.GRAY
            font.draw(game.batch, category.name, x, y + 40f, tabWidth, Align.center, false)
        }
        game.batch.end()
    }

    private fun drawSkinGrid() {
        val skins = game.skinManager.getSkinsForCategory(selectedCategory)
        val selectedSkinId = game.skinManager.getSelectedSkinId(selectedCategory)
        
        val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val startY = Constants.WORLD_HEIGHT - 180f

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
                game.shapeRenderer.rect(x - 5f, y - 5f, skinCardWidth + 10f, skinCardHeight + 10f)
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

            // Skin Preview (Texture)
            val texture = game.assets.getTexture(skin)
            if (texture != null) {
                val previewSize = 120f
                val previewX = x + (skinCardWidth - previewSize) / 2f
                val previewY = y + (skinCardHeight - previewSize) / 2f + 20f
                
                val color = if (isUnlocked) Color.WHITE else Color.GRAY
                game.batch.setColor(color)
                game.batch.draw(texture, previewX, previewY, previewSize, previewSize)
                game.batch.setColor(Color.WHITE)
            }

            // Text
            val font = game.assets.getFont()
            font.data.setScale(1.2f)
            font.color = if (isSelected) Color.CYAN else Color.WHITE
            font.draw(game.batch, skin.displayName, x, y + 40f, skinCardWidth, Align.center, false)
            
            if (!isUnlocked && skin.unlockCondition != null) {
                font.data.setScale(0.9f)
                font.color = Color.GRAY
                font.draw(game.batch, "🔒 ${skin.unlockCondition}", x, y + 20f, skinCardWidth, Align.center, true)
            }
        }
        game.batch.end()
    }

    private fun drawBackButton() {
        val btnWidth = 120f
        val btnHeight = 40f
        val x = 30f
        val y = 30f

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color(0.2f, 0.2f, 0.3f, 1f)
        game.shapeRenderer.rect(x, y, btnWidth, btnHeight)
        game.shapeRenderer.end()

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = Color.GRAY
        game.shapeRenderer.rect(x, y, btnWidth, btnHeight)
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Color.WHITE
        font.data.setScale(1.5f)
        font.draw(game.batch, "BACK", x, y + btnHeight / 2 + 8f, btnWidth, Align.center, false)
        game.batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Constants.WORLD_HEIGHT - Gdx.input.y.toFloat()

            // Check back button
            if (touchX < 160f && touchY < 80f) {
                game.screen = MenuScreen(game)
                dispose()
                return
            }

            // Check category tabs
            val categories = SkinCategory.values()
            val tabWidth = 200f
            val startX = (Constants.WORLD_WIDTH - (categories.size * tabWidth)) / 2f
            val tabY = Constants.WORLD_HEIGHT - 120f
            
            if (touchY > tabY && touchY < tabY + 60f) {
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
            val gridStartY = Constants.WORLD_HEIGHT - 180f

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
