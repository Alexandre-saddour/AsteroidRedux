package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.example.asteroidsredux.AsteroidsGame
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

    init {
        Gdx.input.inputProcessor = inputHandler
        worldManager.spawnAsteroids(Constants.Game.INITIAL_ASTEROID_COUNT)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameRenderer.resize(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        hud.resize(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    override fun drawUi(delta: Float) {
        // Handle back button to return to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.changeScreen(MenuScreen(game), com.example.asteroidsredux.screens.TransitionType.CUSTOMIZE_TO_MENU) // Use transition for consistency? Or just simple switch? MenuScreen uses transitions. Let's use simple switch for now or standard transition. 
            // Actually, Game -> Menu transition wasn't specified. Let's stick to simple switch or use FADE.
            // But wait, the user asked for specific transitions for Menu <-> Customize.
            // For Game -> Menu, let's just use FADE if we want, or keep it simple.
            // The existing code used game.screen = MenuScreen(game).
            // I'll keep it simple for now but use changeScreen if I want transition.
            // Let's just fix the compilation error first.
            game.screen = MenuScreen(game)
            dispose()
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
