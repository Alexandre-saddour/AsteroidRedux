package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.entities.Asteroid
import com.example.asteroidsredux.entities.Bullet
import com.example.asteroidsredux.entities.Ship
import com.example.asteroidsredux.game.CollisionSystem
import com.example.asteroidsredux.game.GameRenderer
import com.example.asteroidsredux.game.Hud
import com.example.asteroidsredux.game.WorldManager
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.progression.PlayerStats
import com.example.asteroidsredux.progression.UpgradeCatalog
import com.example.asteroidsredux.progression.UpgradeDefinition
import com.example.asteroidsredux.progression.UpgradeId
import com.example.asteroidsredux.utils.Button
import com.example.asteroidsredux.utils.ButtonRenderer
import com.example.asteroidsredux.utils.Constants

class GameScreen(game: AsteroidsGame) : BaseScreen(game) {
    private val inputHandler = InputHandler()
    private val playerStats = PlayerStats()
    private val ship = Ship(inputHandler, playerStats, game.assets, game.skinManager)

    // Architectural Components
    private val worldManager = WorldManager(ship)
    private val gameRenderer = GameRenderer(worldManager, game.batch, game.shapeRenderer, game.assets, game.skinManager)
    private val hud = Hud(game.batch, game.shapeRenderer, game.assets, inputHandler, playerStats)
    private val collisionSystem = CollisionSystem(
        worldManager,
        playerStats,
        game.assets,
        onScore = { score += it },
        onXp = { grantXp(it) }
    )

    private var score = 0
    private var isPaused = false
    private var offeredUpgrades = listOf<UpgradeDefinition>()
    private var levelUpMenuOpenedTime = 0f

    // Intro animation state
    private var isInIntroAnimation = true
    private var introTime = 0f
    private var introPhase = 0 // 0 = UI fade, 1 = entity slide
    
    // Store original positions for animation
    private val shipTargetPosition = Vector2()
    private val asteroidStartPositions = mutableMapOf<Asteroid, Vector2>()
    private val asteroidTargetPositions = mutableMapOf<Asteroid, Vector2>()
    
    // Menu UI elements for fade animation (cached from MenuScreen style)
    private val scaleFactor = Constants.UI.SCALE_FACTOR
    private val titleLogo = game.assets.getTitleLogo()
    private val buttonDefault = game.assets.getButtonDefault()
    private val playButtonWidth = 300f * scaleFactor
    private val playButtonHeight = 80f * scaleFactor
    private val customizeButtonWidth = 250f * scaleFactor
    private val customizeButtonHeight = 60f * scaleFactor
    private val buttonSpacing = 30f * scaleFactor
    private val titleOffsetY = 150f * scaleFactor
    private val titleWidth = 600f * scaleFactor
    private val titleHeight = 150f * scaleFactor
    private val playTextScale = 2.5f * scaleFactor
    private val customizeTextScale = 1.8f * scaleFactor

    init {
        Gdx.input.inputProcessor = inputHandler
        worldManager.spawnAsteroids(Constants.Game.INITIAL_ASTEROID_COUNT)
        
        // Setup intro animation
        setupIntroAnimation()
    }
    
    private fun setupIntroAnimation() {
        // Enable intro mode on renderer (fixed camera)
        gameRenderer.isIntroMode = true
        
        // Store ship's target position (center)
        shipTargetPosition.set(ship.position)
        
        // Move ship to start position (below screen)
        ship.position.set(Constants.WORLD_WIDTH / 2, -Constants.SHIP_SIZE * 3)
        
        // For each asteroid, calculate its start position (off-screen based on final position)
        for (asteroid in worldManager.asteroids) {
            val targetPos = Vector2(asteroid.position)
            asteroidTargetPositions[asteroid] = targetPos
            
            // Calculate which edge to slide from based on position
            val centerX = Constants.WORLD_WIDTH / 2
            val centerY = Constants.WORLD_HEIGHT / 2
            
            val startPos = Vector2()
            
            // Determine primary direction based on which quadrant the asteroid is in
            val dx = asteroid.position.x - centerX
            val dy = asteroid.position.y - centerY
            
            if (kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                // Horizontal is dominant - slide from left or right
                if (dx < 0) {
                    // Left side - slide from left edge
                    startPos.set(-asteroid.size * 2, asteroid.position.y)
                } else {
                    // Right side - slide from right edge
                    startPos.set(Constants.WORLD_WIDTH + asteroid.size * 2, asteroid.position.y)
                }
            } else {
                // Vertical is dominant - slide from top or bottom
                if (dy < 0) {
                    // Bottom half - slide from bottom edge
                    startPos.set(asteroid.position.x, -asteroid.size * 2)
                } else {
                    // Top half - slide from top edge
                    startPos.set(asteroid.position.x, Constants.WORLD_HEIGHT + asteroid.size * 2)
                }
            }
            
            asteroidStartPositions[asteroid] = startPos
            
            // Move asteroid to start position
            asteroid.position.set(startPos)
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameRenderer.resize(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        hud.resize(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    // Use shared background during intro for seamless transition from menu
    override val showSharedBackground: Boolean
        get() = isInIntroAnimation

    override fun drawUi(delta: Float) {
        // Draw Parallax Background only after intro is complete
        // During intro, the shared background is used (via showSharedBackground)
        if (!isInIntroAnimation) {
            drawBackground()
        }

        // Handle back button to return to menu (disabled during intro)
        if (!isInIntroAnimation && game.screen == this && Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.changeScreen(MenuScreen(game), com.example.asteroidsredux.screens.TransitionType.CUSTOMIZE_TO_MENU)
            // dispose() is handled by TransitionScreen/Game
            return
        }

        // Handle intro animation
        if (isInIntroAnimation) {
            updateIntroAnimation(delta)
            
            // During entity slide phase, render the game world
            if (introPhase == 1) {
                gameRenderer.render()
            }
            
            // During UI fade phase, render menu UI with fading alpha
            if (introPhase == 0) {
                renderFadingMenuUi()
            }
            
            // Don't render HUD during intro
            return
        }

        if (!isPaused) {
            update(delta)
        } else {
            levelUpMenuOpenedTime += delta
            hud.handleLevelUpInput(offeredUpgrades) { upgrade ->
                applyUpgrade(upgrade)
            }
        }

        gameRenderer.render()
        hud.render(score, isPaused, offeredUpgrades)
    }
    
    private fun updateIntroAnimation(delta: Float) {
        introTime += delta
        
        val uiFadeDuration = Constants.Game.INTRO_UI_FADE_DURATION
        val slideDuration = Constants.Game.INTRO_SLIDE_DURATION
        
        if (introPhase == 0) {
            // Phase 0: UI Fade
            if (introTime >= uiFadeDuration) {
                // Move to phase 1
                introPhase = 1
                introTime = 0f // Reset time for phase 1
            }
            // Don't move entities during UI fade phase
            return
        }
        
        // Phase 1: Entity slide-in
        val progress = (introTime / slideDuration).coerceIn(0f, 1f)
        
        // Use easing for smooth animation
        val easedProgress = Interpolation.pow2Out.apply(progress)
        
        // Animate ship from bottom to center
        val shipStartY = -Constants.SHIP_SIZE * 3
        val shipTargetY = shipTargetPosition.y
        ship.position.x = shipTargetPosition.x
        ship.position.y = shipStartY + (shipTargetY - shipStartY) * easedProgress
        
        // Animate each asteroid from its start position to target
        for (asteroid in worldManager.asteroids) {
            val startPos = asteroidStartPositions[asteroid] ?: continue
            val targetPos = asteroidTargetPositions[asteroid] ?: continue
            
            asteroid.position.x = startPos.x + (targetPos.x - startPos.x) * easedProgress
            asteroid.position.y = startPos.y + (targetPos.y - startPos.y) * easedProgress
            // Sync polygon position for classic skin rendering
            asteroid.polygon.setPosition(asteroid.position.x, asteroid.position.y)
        }
        
        // Check if animation is complete
        if (introTime >= slideDuration) {
            isInIntroAnimation = false
            gameRenderer.isIntroMode = false
            
            // Sync parallax background offset with shared background to prevent jump
            totalOffsetX = -game.backgroundRenderer.scrollX
            totalOffsetY = 0f
            
            // Ensure final positions are exact
            ship.position.set(shipTargetPosition)
            for (asteroid in worldManager.asteroids) {
                val targetPos = asteroidTargetPositions[asteroid] ?: continue
                asteroid.position.set(targetPos)
            }
            
            // Clear animation data
            asteroidStartPositions.clear()
            asteroidTargetPositions.clear()
        }
    }
    
    private fun renderFadingMenuUi() {
        val uiFadeDuration = Constants.Game.INTRO_UI_FADE_DURATION
        val fadeProgress = (introTime / uiFadeDuration).coerceIn(0f, 1f)
        val alpha = 1f - Interpolation.pow2In.apply(fadeProgress)
        
        if (alpha <= 0f) return
        
        val centerX = Constants.WORLD_WIDTH / 2
        val centerY = Constants.WORLD_HEIGHT / 2
        
        // Create button objects matching MenuScreen layout
        val playBtn = Button(
            x = centerX - playButtonWidth / 2,
            y = centerY - playButtonHeight / 2,
            width = playButtonWidth,
            height = playButtonHeight,
            text = "PLAY",
            textColor = Color(Color.CYAN.r, Color.CYAN.g, Color.CYAN.b, alpha),
            textScale = playTextScale,
            texture = buttonDefault,
            pressedTexture = buttonDefault
        )
        
        val customizeBtn = Button(
            x = centerX - customizeButtonWidth / 2,
            y = playBtn.y - customizeButtonHeight - buttonSpacing,
            width = customizeButtonWidth,
            height = customizeButtonHeight,
            text = "Skin Selection",
            textColor = Color(1f, 1f, 1f, alpha),
            textScale = customizeTextScale,
            texture = buttonDefault,
            pressedTexture = buttonDefault
        )
        
        // Draw buttons with alpha
        game.batch.setColor(1f, 1f, 1f, alpha)
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), playBtn, false)
        ButtonRenderer.draw(game.shapeRenderer, game.batch, game.assets.getFont(), customizeBtn, false)
        
        // Draw title logo with alpha
        game.batch.begin()
        game.batch.setColor(1f, 1f, 1f, alpha)
        game.batch.draw(
            titleLogo,
            centerX - titleWidth / 2,
            centerY + titleOffsetY - titleHeight / 2,
            titleWidth,
            titleHeight
        )
        game.batch.setColor(1f, 1f, 1f, 1f)
        game.batch.end()
    }

    private var totalOffsetX = -game.backgroundRenderer.scrollX
    private var totalOffsetY = 0f

    private fun drawBackground() {
        val bgSkin = game.skinManager.getSelectedBackgroundSkin()
        val region = game.assets.getBackgroundTexture(com.example.asteroidsredux.skins.BackgroundSkinId.valueOf(bgSkin.id))

        if (region != null) {
            game.batch.begin()
            game.batch.disableBlending()

            val tileW = region.regionWidth.toFloat()
            val tileH = region.regionHeight.toFloat()

            // Parallax factor (0.2 means background moves at 20% of ship speed)
            val parallaxFactor = 0.2f

            // Accumulate offset based on ship velocity
            // We use Gdx.graphics.deltaTime for smooth rendering update
            // Note: We should ideally do this in update() but doing it here is fine for visual effect
            val delta = Gdx.graphics.deltaTime
            totalOffsetX -= ship.velocity.x * delta * parallaxFactor
            totalOffsetY -= ship.velocity.y * delta * parallaxFactor

            // Calculate the starting position for the first tile
            // We use modulo to find the offset within a single tile
            // We subtract tileW/tileH to ensure we always have a buffer on the left/top
            var startX = (totalOffsetX % tileW)
            if (startX > 0) startX -= tileW

            var startY = (totalOffsetY % tileH)
            if (startY > 0) startY -= tileH

            // Draw enough tiles to cover the screen
            val cols = (Constants.WORLD_WIDTH / tileW).toInt() + 2
            val rows = (Constants.WORLD_HEIGHT / tileH).toInt() + 2

            for (col in 0 until cols) {
                for (row in 0 until rows) {
                    game.batch.draw(
                        region,
                        startX + col * tileW,
                        startY + row * tileH,
                        tileW,
                        tileH
                    )
                }
            }

            game.batch.enableBlending()
            game.batch.end()
        }
    }

    private fun update(delta: Float) {
        // Shooting Logic
        shootTimer -= delta
        if (inputHandler.isShooting && !ship.isDead) {
             shoot()
        }

        worldManager.update(delta)
        collisionSystem.checkCollisions()

        if (worldManager.asteroids.size == 0) {
            worldManager.spawnAsteroids(Constants.Game.WAVE_SPAWN_MULTIPLIER + playerStats.level)
        }

        if (ship.isDead) {
            game.screen = GameOverScreen(game, score)
            dispose()
        }
    }

    private var shootTimer = 0f
    private fun shoot() {
        if (shootTimer <= 0) {
            val baseAngle = ship.angle
            val count = 1 + playerStats.multiShotLevel

            for (i in 0 until count) {
                val bullet = Bullet()
                val angleOffset = if (count == 1) 0f else {
                    val maxAngle = when(count) {
                        2 -> 8f
                        3 -> 12f
                        4 -> 16f
                        else -> 0f
                    } * MathUtils.degreesToRadians

                    MathUtils.lerp(-maxAngle, maxAngle, i / (count - 1).toFloat())
                }

                bullet.init(ship.nose.x, ship.nose.y, baseAngle + angleOffset, playerStats)
                worldManager.addBullet(bullet)
            }

            game.assets.getShootSound().play()
            shootTimer = playerStats.fireCooldown
        }
    }

    private fun grantXp(amount: Int) {
        val bonusMultiplier = 1f + (0.05f * playerStats.magnetLevel)
        val finalXp = (amount * bonusMultiplier).toInt()

        playerStats.currentXp += finalXp
        checkLevelUp()
    }

    private fun checkLevelUp() {
        while (playerStats.currentXp >= playerStats.xpToNextLevel && playerStats.level < 15) {
            playerStats.currentXp -= playerStats.xpToNextLevel
            playerStats.level++
            playerStats.xpToNextLevel = 40 + 10 * (playerStats.level - 1)

            triggerLevelUp()
        }
    }

    private fun triggerLevelUp() {
        isPaused = true
        offeredUpgrades = rollUpgradeOptions()
        levelUpMenuOpenedTime = 0f
    }

    private fun rollUpgradeOptions(): List<UpgradeDefinition> {
        val all = UpgradeCatalog.upgrades.filter {
            (playerStats.upgradeLevels[it.id] ?: 0) < it.maxLevel
        }
        val shuffled = all.shuffled()
        return shuffled.take(3)
    }

    private fun applyUpgrade(def: UpgradeDefinition) {
        val currentLevel = playerStats.upgradeLevels.getOrDefault(def.id, 0)
        playerStats.upgradeLevels[def.id] = currentLevel + 1

        when (def.id) {
            UpgradeId.DAMAGE -> playerStats.baseDamage *= 1.2f // +20%
            UpgradeId.FIRE_RATE -> playerStats.fireCooldown *= 0.88f // -12%
            UpgradeId.BULLET_SPEED -> playerStats.bulletSpeed *= 1.25f // +25%
            UpgradeId.BULLET_RANGE -> playerStats.bulletLifetimeMultiplier *= 1.3f // +30%
            UpgradeId.MULTI_SHOT -> playerStats.multiShotLevel++
            UpgradeId.PIERCE -> playerStats.pierceLevel++
            UpgradeId.MOVE_SPEED -> {
                playerStats.shipAcceleration *= 1.15f
                playerStats.shipMaxSpeed *= 1.15f
            }
            UpgradeId.MAX_HP -> {
                playerStats.maxHp++
                if (playerStats.currentHp < playerStats.maxHp) playerStats.currentHp++
            }
            UpgradeId.SHIELD -> {
                playerStats.shieldLevel++
                playerStats.shieldRechargeTime = if (playerStats.shieldLevel == 2) 7f else 12f
            }
            UpgradeId.MAGNET -> playerStats.magnetLevel++
            UpgradeId.EXPLOSION_RADIUS -> playerStats.explosionRadiusLevel++
            UpgradeId.BULLET_SIZE -> playerStats.bulletSizeMultiplier += 0.5f // +50% base size per level
        }

        isPaused = false
    }
}
