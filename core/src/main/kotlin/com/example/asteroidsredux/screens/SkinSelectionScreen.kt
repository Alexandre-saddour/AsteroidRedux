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
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined
        game.shapeRenderer.projectionMatrix = camera.combined

        drawCategoryTabs()
        drawSkinGrid()
        drawBackButton()
        
        handleInput()
    }

    private fun drawCategoryTabs() {
        val categories = SkinCategory.values()
        val tabWidth = Constants.WORLD_WIDTH / categories.size

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for ((i, category) in categories.withIndex()) {
            val x = i * tabWidth
            val isSelected = category == selectedCategory
            game.shapeRenderer.color = if (isSelected) Color.CYAN else Color.DARK_GRAY
            game.shapeRenderer.rect(x, Constants.WORLD_HEIGHT - tabHeight, tabWidth - 2f, tabHeight - 2f)
        }
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Color.WHITE
        font.data.setScale(2f)
        for ((i, category) in categories.withIndex()) {
            val x = i * tabWidth
            font.draw(game.batch, category.name, x, Constants.WORLD_HEIGHT - tabHeight / 2 + 10f, tabWidth, Align.center, false)
        }
        game.batch.end()
    }

    private fun drawSkinGrid() {
        val skins = game.skinManager.getSkinsForCategory(selectedCategory)
        val selectedSkinId = game.skinManager.getSelectedSkinId(selectedCategory)
        
        val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val startY = Constants.WORLD_HEIGHT - tabHeight - 50f

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            val isUnlocked = game.skinManager.isUnlocked(selectedCategory, skin.id)
            val isSelected = skin.id == selectedSkinId

            // Card background
            game.shapeRenderer.color = when {
                isSelected -> Color.CYAN.cpy().apply { a = 0.3f }
                isUnlocked -> Color.DARK_GRAY
                else -> Color.DARK_GRAY.cpy().apply { a = 0.5f }
            }
            game.shapeRenderer.rect(x, y, skinCardWidth, skinCardHeight)
        }
        game.shapeRenderer.end()

        // Draw borders
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            val isSelected = skin.id == selectedSkinId
            game.shapeRenderer.color = if (isSelected) Color.CYAN else Color.WHITE
            game.shapeRenderer.rect(x, y, skinCardWidth, skinCardHeight)
        }
        game.shapeRenderer.end()

        // Draw skin names
        game.batch.begin()
        val font = game.assets.getFont()
        font.data.setScale(1.5f)
        for ((i, skin) in skins.withIndex()) {
            val row = i / cardsPerRow
            val col = i % cardsPerRow
            val x = startX + col * (skinCardWidth + cardSpacing)
            val y = startY - row * (skinCardHeight + cardSpacing) - skinCardHeight

            val isUnlocked = game.skinManager.isUnlocked(selectedCategory, skin.id)
            font.color = if (isUnlocked) Color.WHITE else Color.GRAY
            font.draw(game.batch, skin.displayName, x, y + 40f, skinCardWidth, Align.center, false)
            
            if (!isUnlocked && skin.unlockCondition != null) {
                font.data.setScale(1f)
                font.color = Color.GRAY
                font.draw(game.batch, "🔒 ${skin.unlockCondition}", x, y + 20f, skinCardWidth, Align.center, true)
                font.data.setScale(1.5f)
            }
        }
        game.batch.end()
    }

    private fun drawBackButton() {
        val btnWidth = 150f
        val btnHeight = 50f
        val x = 20f
        val y = 20f

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = Color.DARK_GRAY
        game.shapeRenderer.rect(x, y, btnWidth, btnHeight)
        game.shapeRenderer.end()

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = Color.WHITE
        game.shapeRenderer.rect(x, y, btnWidth, btnHeight)
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Color.WHITE
        font.data.setScale(2f)
        font.draw(game.batch, "← Back", x, y + btnHeight / 2 + 10f, btnWidth, Align.center, false)
        game.batch.end()
    }

    private fun handleInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Constants.WORLD_HEIGHT - Gdx.input.y.toFloat()

            // Check back button
            if (touchX < 170f && touchY < 70f) {
                game.screen = MenuScreen(game)
                dispose()
                return
            }

            // Check category tabs
            val categories = SkinCategory.values()
            val tabWidth = Constants.WORLD_WIDTH / categories.size
            if (touchY > Constants.WORLD_HEIGHT - tabHeight) {
                val tabIndex = (touchX / tabWidth).toInt().coerceIn(0, categories.size - 1)
                selectedCategory = categories[tabIndex]
                return
            }

            // Check skin cards
            val skins = game.skinManager.getSkinsForCategory(selectedCategory)
            val totalWidth = cardsPerRow * skinCardWidth + (cardsPerRow - 1) * cardSpacing
            val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
            val startY = Constants.WORLD_HEIGHT - tabHeight - 50f

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
}
