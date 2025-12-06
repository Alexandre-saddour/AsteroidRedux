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
import com.example.asteroidsredux.progression.UpgradeId
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

        // Darker overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.95f)
        shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        shapeRenderer.end()

        val totalWidth = offeredUpgrades.size * Constants.UI.CARD_WIDTH + (offeredUpgrades.size - 1) * Constants.UI.CARD_SPACING
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val centerY = Constants.WORLD_HEIGHT / 2f
        val cardY = centerY - Constants.UI.CARD_HEIGHT / 2f

        // Determine which card is selected (hovered)
        val touchX = Gdx.input.x.toFloat()
        val touchY = Gdx.input.y.toFloat()
        val worldPos = uiCamera.unproject(com.badlogic.gdx.math.Vector3(touchX, touchY, 0f))
        
        var selectedIndex = -1
        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            if (worldPos.x >= x && worldPos.x <= x + Constants.UI.CARD_WIDTH &&
                worldPos.y >= cardY && worldPos.y <= cardY + Constants.UI.CARD_HEIGHT) {
                selectedIndex = i
                break
            }
        }

        batch.begin()
        val cardTexture = assets.getCard()
        val cardSelectedTexture = assets.getCardSelected()

        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            val texture = if (i == selectedIndex) cardSelectedTexture else cardTexture
            batch.draw(texture, x, cardY, Constants.UI.CARD_WIDTH, Constants.UI.CARD_HEIGHT)
        }
        
        val font = assets.getFont()
        val cardTop = centerY + Constants.UI.CARD_HEIGHT / 2f

        // Title "LEVEL UP!" with glow effect (simulated by multiple draws)
        font.data.setScale(Constants.UI.FONT_SCALE_TITLE * 1.2f)
        val titleText = "LEVEL UP!"
        // Move title higher up
        val titleY = cardTop + 150f
        
        // Shadow/Glow
        font.color = Color.CYAN.cpy().apply { a = 0.3f }
        font.draw(batch, titleText, 4f, titleY - 4f, Constants.WORLD_WIDTH, Align.center, false)
        font.draw(batch, titleText, -4f, titleY + 4f, Constants.WORLD_WIDTH, Align.center, false)
        
        // Main Title
        font.color = Color.WHITE
        font.draw(batch, titleText, 0f, titleY, Constants.WORLD_WIDTH, Align.center, false)

        for ((i, upgrade) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            val cardCenterX = x + Constants.UI.CARD_WIDTH / 2f

            // Card Title
            font.data.setScale(Constants.UI.FONT_SCALE_HEADER)
            font.color = Color.CYAN
            font.draw(batch, upgrade.displayName, x, cardTop - 40f, Constants.UI.CARD_WIDTH, Align.center, false)

            // Level
            val nextLevel = (playerStats.upgradeLevels[upgrade.id] ?: 0) + 1
            font.data.setScale(Constants.UI.FONT_SCALE_SMALL)
            font.color = Color.LIGHT_GRAY
            font.draw(batch, "LVL $nextLevel", x, cardTop - 85f, Constants.UI.CARD_WIDTH, Align.center, false)

            // Icon is drawn later at cardTop - 160f with size 40f (radius approx 40 or size 40? Function uses size as radius/extent)
            // If size is 40, it extends from -120 to -200 relative to cardTop.
            
            // Description
            // Start description below the icon
            val descY = cardTop - 230f
            val desc = upgrade.descriptionPerLevel.getOrNull(nextLevel - 1) ?: ""
            font.data.setScale(Constants.UI.FONT_SCALE_NORMAL)
            font.color = Color.WHITE
            // Wrap text manually or use a smaller width
            font.draw(batch, desc, x + 20f, descY, Constants.UI.CARD_WIDTH - 40f, Align.center, true)
        }
        batch.end()
        
        // Draw Icons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.CYAN
        for ((i, upgrade) in offeredUpgrades.withIndex()) {
            val x = startX + i * (Constants.UI.CARD_WIDTH + Constants.UI.CARD_SPACING)
            val cardCenterX = x + Constants.UI.CARD_WIDTH / 2f
            val cardTop = centerY + Constants.UI.CARD_HEIGHT / 2f
            
            // Center icon vertically in the upper-middle section
            val iconY = cardTop - 160f
            val iconSize = 40f
            
            drawUpgradeIcon(upgrade.id, cardCenterX, iconY, iconSize)
        }
        shapeRenderer.end()
    }

    private fun drawUpgradeIcon(id: UpgradeId, cx: Float, cy: Float, size: Float) {
        when (id) {
            UpgradeId.DAMAGE -> {
                // Crosshair / Plus
                shapeRenderer.line(cx - size, cy, cx + size, cy)
                shapeRenderer.line(cx, cy - size, cx, cy + size)
                shapeRenderer.circle(cx, cy, size * 0.6f)
            }
            UpgradeId.FIRE_RATE -> {
                // Three parallel bullets/lines
                shapeRenderer.line(cx - size * 0.5f, cy - size, cx - size * 0.5f, cy + size)
                shapeRenderer.line(cx, cy - size, cx, cy + size)
                shapeRenderer.line(cx + size * 0.5f, cy - size, cx + size * 0.5f, cy + size)
            }
            UpgradeId.BULLET_SPEED -> {
                // Arrow pointing right
                shapeRenderer.line(cx - size, cy, cx + size, cy)
                shapeRenderer.line(cx + size, cy, cx + size * 0.5f, cy + size * 0.5f)
                shapeRenderer.line(cx + size, cy, cx + size * 0.5f, cy - size * 0.5f)
            }
            UpgradeId.BULLET_RANGE -> {
                // Target with arrow
                shapeRenderer.circle(cx, cy, size * 0.3f)
                shapeRenderer.line(cx - size, cy, cx - size * 0.4f, cy)
                shapeRenderer.line(cx + size * 0.4f, cy, cx + size, cy)
                shapeRenderer.line(cx + size, cy, cx + size * 0.7f, cy + size * 0.3f)
                shapeRenderer.line(cx + size, cy, cx + size * 0.7f, cy - size * 0.3f)
            }
            UpgradeId.MULTI_SHOT -> {
                // Fan shape
                shapeRenderer.line(cx, cy - size, cx, cy + size)
                shapeRenderer.line(cx, cy - size, cx - size * 0.8f, cy + size * 0.8f)
                shapeRenderer.line(cx, cy - size, cx + size * 0.8f, cy + size * 0.8f)
            }
            UpgradeId.PIERCE -> {
                // Arrow going through a line
                shapeRenderer.line(cx, cy - size, cx, cy + size) // Vertical line
                shapeRenderer.line(cx - size, cy, cx + size, cy) // Arrow shaft
                shapeRenderer.line(cx + size, cy, cx + size * 0.6f, cy + size * 0.4f) // Arrow head
                shapeRenderer.line(cx + size, cy, cx + size * 0.6f, cy - size * 0.4f)
            }
            UpgradeId.BULLET_SIZE -> {
                // Small circle and big circle
                shapeRenderer.circle(cx - size * 0.5f, cy, size * 0.3f)
                shapeRenderer.circle(cx + size * 0.5f, cy, size * 0.6f)
            }
            UpgradeId.MOVE_SPEED -> {
                // Chevron / Thruster
                shapeRenderer.line(cx - size, cy - size, cx, cy)
                shapeRenderer.line(cx - size, cy + size, cx, cy)
                shapeRenderer.line(cx, cy - size, cx + size, cy)
                shapeRenderer.line(cx, cy + size, cx + size, cy)
            }
            UpgradeId.MAX_HP -> {
                // Heart-ish or Cross
                shapeRenderer.rect(cx - size * 0.3f, cy - size * 0.8f, size * 0.6f, size * 1.6f)
                shapeRenderer.rect(cx - size * 0.8f, cy - size * 0.3f, size * 1.6f, size * 0.6f)
            }
            UpgradeId.SHIELD -> {
                // Shield outline
                shapeRenderer.circle(cx, cy, size)
                shapeRenderer.circle(cx, cy, size * 0.7f)
            }
            UpgradeId.MAGNET -> {
                // U shape
                shapeRenderer.arc(cx, cy, size, 180f, 180f)
                shapeRenderer.line(cx - size, cy, cx - size, cy + size * 0.5f)
                shapeRenderer.line(cx + size, cy, cx + size, cy + size * 0.5f)
            }
            UpgradeId.EXPLOSION_RADIUS -> {
                // Star / Explosion
                shapeRenderer.circle(cx, cy, size * 0.4f)
                shapeRenderer.line(cx - size, cy, cx + size, cy)
                shapeRenderer.line(cx, cy - size, cx, cy + size)
                shapeRenderer.line(cx - size * 0.7f, cy - size * 0.7f, cx + size * 0.7f, cy + size * 0.7f)
                shapeRenderer.line(cx - size * 0.7f, cy + size * 0.7f, cx + size * 0.7f, cy - size * 0.7f)
            }
        }
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
