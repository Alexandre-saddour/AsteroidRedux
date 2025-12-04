package com.example.asteroidsredux.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.progression.PlayerStats
import com.example.asteroidsredux.progression.UpgradeDefinition
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class Hud(
    private val batch: SpriteBatch,
    private val shapeRenderer: ShapeRenderer,
    private val assets: Assets,
    private val inputHandler: InputHandler,
    private val playerStats: PlayerStats
) {
    private val uiCamera = OrthographicCamera()

    init {
        uiCamera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    fun resize(width: Float, height: Float) {
        uiCamera.setToOrtho(false, width, height)
    }

    fun render(score: Int, isPaused: Boolean, offeredUpgrades: List<UpgradeDefinition>) {
        uiCamera.update()
        batch.projectionMatrix = uiCamera.combined
        shapeRenderer.projectionMatrix = uiCamera.combined

        // Draw HUD
        batch.begin()
        val font = assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(Constants.UI.FONT_SCALE_SMALL)
        font.draw(batch, "Score: $score", 20f, Constants.WORLD_HEIGHT - 20f)
        font.draw(batch, "Lv. ${playerStats.level}", 20f, Constants.WORLD_HEIGHT - 50f)
        font.draw(batch, "HP: ${playerStats.currentHp}/${playerStats.maxHp}", 20f, Constants.WORLD_HEIGHT - 80f)
        batch.end()

        // XP Bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.DARK_GRAY
        shapeRenderer.rect(20f, Constants.WORLD_HEIGHT - 70f, 200f, 10f)

        if (playerStats.xpToNextLevel > 0) {
            shapeRenderer.color = Color.CYAN
            val progress = playerStats.currentXp.toFloat() / playerStats.xpToNextLevel
            shapeRenderer.rect(20f, Constants.WORLD_HEIGHT - 70f, 200f * progress, 10f)
        }
        shapeRenderer.end()

        // Draw Controls
        drawControls()

        // Draw Level Up Overlay
        if (isPaused) {
            drawLevelUpOverlay(offeredUpgrades)
        }
    }

    private fun drawControls() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        inputHandler.thrustButton.render(shapeRenderer)
        inputHandler.joystick.render(shapeRenderer)
        shapeRenderer.end()

        if (inputHandler.thrustButton.isPressed) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            val btn = inputHandler.thrustButton
            shapeRenderer.color = btn.color.cpy().apply { a = 0.3f }
            shapeRenderer.circle(btn.position.x, btn.position.y, btn.radius * 0.7f, 40)
            shapeRenderer.end()
        }

        batch.begin()
        val font = assets.getFont()
        font.color = inputHandler.thrustButton.color
        font.data.setScale(Constants.UI.FONT_SCALE_BUTTON)
        val btn = inputHandler.thrustButton
        font.draw(batch, btn.label, btn.position.x - 20f, btn.position.y + 15f)
        batch.end()
    }

    private fun drawLevelUpOverlay(offeredUpgrades: List<UpgradeDefinition>) {
        Gdx.gl.glEnable(GL20.GL_BLEND)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.9f)
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        val totalWidth = offeredUpgrades.size * Constants.UI.CARD_WIDTH + (offeredUpgrades.size - 1) * Constants.UI.CARD_SPACING
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val centerY = Constants.WORLD_HEIGHT / 2f

        shapeRenderer.color = Color.DARK_GRAY
        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            val y = centerY - Constants.UI.CARD_HEIGHT / 2f
            shapeRenderer.rect(x, y, Constants.UI.CARD_WIDTH, Constants.UI.CARD_HEIGHT)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            val y = centerY - Constants.UI.CARD_HEIGHT / 2f
            shapeRenderer.rect(x, y, Constants.UI.CARD_WIDTH, Constants.UI.CARD_HEIGHT)
        }
        shapeRenderer.end()

        batch.begin()
        val font = assets.getFont()
        font.color = Color.WHITE
        val cardTop = centerY + Constants.UI.CARD_HEIGHT / 2f

        font.data.setScale(Constants.UI.FONT_SCALE_TITLE)
        font.draw(batch, "LEVEL UP!", 0f, cardTop + 80f, Constants.WORLD_WIDTH, Align.center, false)

        for ((i, upgrade) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)

            font.data.setScale(Constants.UI.FONT_SCALE_HEADER)
            font.draw(batch, upgrade.displayName, x, cardTop - 60f, Constants.UI.CARD_WIDTH, Align.center, false)

            val nextLevel = (playerStats.upgradeLevels[upgrade.id] ?: 0) + 1
            font.data.setScale(Constants.UI.FONT_SCALE_NORMAL)
            font.draw(batch, "Level $nextLevel", x, cardTop - 130f, Constants.UI.CARD_WIDTH, Align.center, false)

            val desc = upgrade.descriptionPerLevel.getOrNull(nextLevel - 1) ?: ""
            font.data.setScale(Constants.UI.FONT_SCALE_NORMAL)
            font.draw(batch, desc, x + 20f, cardTop - 200f, Constants.UI.CARD_WIDTH - 40f, Align.center, true)
        }
        batch.end()
    }

    fun handleLevelUpInput(offeredUpgrades: List<UpgradeDefinition>, onSelect: (UpgradeDefinition) -> Unit) {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.input.y.toFloat()
            val worldPos = uiCamera.unproject(com.badlogic.gdx.math.Vector3(touchX, touchY, 0f))

            val totalWidth = offeredUpgrades.size * Constants.UI.CARD_WIDTH + (offeredUpgrades.size - 1) * Constants.UI.CARD_SPACING
            val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
            val centerY = Constants.WORLD_HEIGHT / 2f
            val cardY = centerY - Constants.UI.CARD_HEIGHT / 2f

            for ((i, upgrade) in offeredUpgrades.withIndex()) {
                val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
                if (worldPos.x >= x && worldPos.x <= x + Constants.UI.CARD_WIDTH &&
                    worldPos.y >= cardY && worldPos.y <= cardY + Constants.UI.CARD_HEIGHT) {
                    onSelect(upgrade)
                    break
                }
            }
        }
    }
}
