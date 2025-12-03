package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.example.asteroidsredux.AsteroidsGame
import com.example.asteroidsredux.entities.Asteroid
import com.example.asteroidsredux.entities.Bullet
import com.example.asteroidsredux.entities.Particle
import com.example.asteroidsredux.entities.Ship
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.utils.Constants

import com.example.asteroidsredux.progression.PlayerStats
import com.example.asteroidsredux.progression.UpgradeCatalog
import com.example.asteroidsredux.progression.UpgradeDefinition
import com.example.asteroidsredux.progression.UpgradeId

class GameScreen(private val game: AsteroidsGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val inputHandler = InputHandler()

    private val playerStats = PlayerStats()
    private val ship = Ship(inputHandler, playerStats)

    private val asteroids = Array<Asteroid>()
    private val bullets = Array<Bullet>()
    private val particles = Array<Particle>()

    private var score = 0
    // Level tracked in playerStats

    private var isPaused = false
    private var offeredUpgrades = listOf<UpgradeDefinition>()

    // UI layout constants
    private val cardWidth = 350f
    private val cardHeight = 450f
    private val cardSpacing = 40f

    init {
        Constants.WORLD_HEIGHT = Gdx.graphics.height.toFloat()
        Constants.WORLD_WIDTH = Gdx.graphics.width.toFloat()
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        Gdx.input.inputProcessor = inputHandler
        spawnAsteroids(3)
    }

    private fun spawnAsteroids(count: Int) {
        for (i in 0 until count) {
            var x = MathUtils.random(Constants.WORLD_WIDTH)
            var y = MathUtils.random(Constants.WORLD_HEIGHT)
            // Avoid spawning on ship
            while (MathUtils.isEqual(x, ship.position.x, 10f) && MathUtils.isEqual(y, ship.position.y, 10f)) {
                x = MathUtils.random(Constants.WORLD_WIDTH)
                y = MathUtils.random(Constants.WORLD_HEIGHT)
            }
            asteroids.add(Asteroid(Constants.ASTEROID_SIZE_LARGE, x, y))
        }
    }

    override fun render(delta: Float) {
        if (!isPaused) {
            update(delta)
        } else {
            // Handle upgrade selection input
            handleUpgradeInput()
        }
        draw()
        if (isPaused) {
            drawLevelUpOverlay()
        }
    }

    private fun update(delta: Float) {
        // Update joystick rotation
        inputHandler.updateRotation()

        // Input for shooting
        if (inputHandler.isShooting && !ship.isDead) {
            // Simple rate limiting could be added here
            // For now, let's just shoot on "just touched" logic or similar if we want semi-auto
            // But InputHandler sets isShooting to true on touchDown.
            // We need to ensure we don't spawn 60 bullets a second.
            // Let's implement a simple timer in Ship or here.
            // For simplicity, let's modify logic: shoot only on new taps or limit rate.
            // Given "Tap court = tir; appui long = tir auto optionnel", let's do a simple cooldown.
        }

        // Better shooting logic:
        shootTimer -= delta
        if (inputHandler.isShooting && !ship.isDead) {
             shoot()
        }

        ship.update(delta)

        // Update entities
        val asteroidIter = asteroids.iterator()
        while (asteroidIter.hasNext()) {
            val asteroid = asteroidIter.next()
            asteroid.update(delta)
        }

        val bulletIter = bullets.iterator()
        while (bulletIter.hasNext()) {
            val bullet = bulletIter.next()
            bullet.update(delta)
            if (!bullet.active) bulletIter.remove()
        }

        val particleIter = particles.iterator()
        while (particleIter.hasNext()) {
            val particle = particleIter.next()
            particle.update(delta)
            if (!particle.active) particleIter.remove()
        }

        checkCollisions()

        if (asteroids.size == 0) {
            // Wave clear logic? Maybe just spawn more.
            // User didn't specify wave logic changes, but we should probably keep spawning.
            spawnAsteroids(2 + playerStats.level)
        }

        if (ship.isDead) {
            // Wait a bit or go to game over immediately?
            // Let's wait for particles to fade or just go.
            // For now, immediate transition after a short delay would be nice, but let's do immediate for MVP.
            game.screen = GameOverScreen(game, score)
            dispose()
        }
    }

    private var shootTimer = 0f
    private fun shoot() {
        if (shootTimer <= 0) {
            // Multi-shot logic
            val baseAngle = ship.angle
            val count = 1 + playerStats.multiShotLevel
            // Spread angles: 1->0, 2->±8, 3->0,±12 (wait, user said: L1:+1(2 total) ±8deg. L2:+2(3 total) ±12deg. L3:+3(4 total) ±16deg)
            // Let's implement spread logic
            val spread = when (playerStats.multiShotLevel) {
                1 -> 8f * MathUtils.degreesToRadians // 2 bullets
                2 -> 12f * MathUtils.degreesToRadians // 3 bullets
                3 -> 16f * MathUtils.degreesToRadians // 4 bullets
                else -> 0f
            }

            for (i in 0 until count) {
                val bullet = Bullet()
                // Distribute evenly within [-spread, +spread]?
                // Or specific fixed angles?
                // Let's do simple distribution centered on nose.
                // If count 2: -8, +8.
                // If count 3: -12, 0, +12.
                // If count 4: -16, -5.3, +5.3, +16.

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
                bullets.add(bullet)
            }

            game.assets.getShootSound().play()
            shootTimer = playerStats.fireCooldown
        }
    }

    private fun checkCollisions() {
        if (ship.isDead) return

        // Bullet <-> Asteroid
        val bulletIter = bullets.iterator()
        while (bulletIter.hasNext()) {
            val bullet = bulletIter.next()
            val asteroidIter = asteroids.iterator()
            while (asteroidIter.hasNext()) {
                val asteroid = asteroidIter.next()
                if (asteroid.polygon.contains(bullet.position)) {
                    asteroid.active = false
                    asteroidIter.remove()

                    handleAsteroidDestruction(asteroid)

                    // Pierce mechanic: bullet can hit multiple asteroids
                    if (bullet.remainingHits > 0) {
                        bullet.remainingHits--
                    } else {
                        bullet.active = false
                        bulletIter.remove()
                        break
                    }
                }
            }
        }


        // Ship <-> Asteroid
        for (asteroid in asteroids) {
            // Simple circle collision for ship
            val dist = ship.position.dst(asteroid.position)
            if (dist < Constants.SHIP_SIZE + asteroid.size) {
                // Take damage
                playerStats.currentHp--
                game.assets.getExplosionSound().play()
                spawnParticles(ship.position.x, ship.position.y, 10)

                // Destroy the asteroid that hit us
                asteroid.active = false
                asteroids.removeValue(asteroid, true)

                // Check if dead
                if (playerStats.currentHp <= 0) {
                    ship.isDead = true
                    spawnParticles(ship.position.x, ship.position.y, 30)
                }
                break
            }
        }
    }

    private fun handleAsteroidDestruction(asteroid: Asteroid) {
        game.assets.getExplosionSound().play()
        spawnParticles(asteroid.position.x, asteroid.position.y, 10)

        // Grant XP for destroying this asteroid
        grantXp(asteroid.xpValue)

        when (asteroid.size) {
            Constants.ASTEROID_SIZE_LARGE -> {
                score += Constants.ASTEROID_SCORE_LARGE
                asteroids.add(Asteroid(Constants.ASTEROID_SIZE_MEDIUM, asteroid.position.x, asteroid.position.y))
                asteroids.add(Asteroid(Constants.ASTEROID_SIZE_MEDIUM, asteroid.position.x, asteroid.position.y))
            }
            Constants.ASTEROID_SIZE_MEDIUM -> {
                score += Constants.ASTEROID_SCORE_MEDIUM
                asteroids.add(Asteroid(Constants.ASTEROID_SIZE_SMALL, asteroid.position.x, asteroid.position.y))
                asteroids.add(Asteroid(Constants.ASTEROID_SIZE_SMALL, asteroid.position.x, asteroid.position.y))
            }
            Constants.ASTEROID_SIZE_SMALL -> {
                score += Constants.ASTEROID_SCORE_SMALL
            }
        }

        // Explosion Radius Upgrade - damage nearby asteroids
        if (playerStats.explosionRadiusLevel > 0) {
            val baseRadius = 80f
            val radius = baseRadius * (1f + 0.25f * playerStats.explosionRadiusLevel)

            val nearbyAsteroids = asteroids.filter {
                it.active && it.position.dst(asteroid.position) <= radius
            }

            for (nearby in nearbyAsteroids) {
                // Only destroy small asteroids with explosion
                if (nearby.size == Constants.ASTEROID_SIZE_SMALL) {
                    nearby.active = false
                    asteroids.removeValue(nearby, true)
                    handleAsteroidDestruction(nearby) // Recursive for XP and potential chain reactions
                }
            }
        }
    }

    private fun spawnParticles(x: Float, y: Float, count: Int) {
        for (i in 0 until count) {
            val p = Particle()
            val angle = MathUtils.random(0f, MathUtils.PI2)
            val speed = MathUtils.random(1f, Constants.PARTICLE_SPEED)
            p.init(x, y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
            particles.add(p)
        }
    }

    private fun draw() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.shapeRenderer.projectionMatrix = camera.combined
        game.batch.projectionMatrix = camera.combined

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        ship.render(game.shapeRenderer)
        for (asteroid in asteroids) asteroid.render(game.shapeRenderer)
        game.shapeRenderer.end()

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (bullet in bullets) bullet.render(game.shapeRenderer)
        for (particle in particles) particle.render(game.shapeRenderer)
        game.shapeRenderer.end()

        game.batch.begin()
        val font = game.assets.getFont()
        font.color = Constants.SHIP_COLOR
        font.data.setScale(1.2f) // Larger for HUD readability
        font.draw(game.batch, "Score: $score", 20f, Constants.WORLD_HEIGHT - 20f)
        font.draw(game.batch, "Lv. ${playerStats.level}", 20f, Constants.WORLD_HEIGHT - 50f)
        font.draw(game.batch, "HP: ${playerStats.currentHp}/${playerStats.maxHp}", 20f, Constants.WORLD_HEIGHT - 80f)
        game.batch.end()

        // XP Bar
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = com.badlogic.gdx.graphics.Color.DARK_GRAY
        game.shapeRenderer.rect(20f, Constants.WORLD_HEIGHT - 70f, 200f, 10f)

        if (playerStats.xpToNextLevel > 0) {
            game.shapeRenderer.color = com.badlogic.gdx.graphics.Color.CYAN
            val progress = playerStats.currentXp.toFloat() / playerStats.xpToNextLevel
            game.shapeRenderer.rect(20f, Constants.WORLD_HEIGHT - 70f, 200f * progress, 10f)
        }
        game.shapeRenderer.end()

        // Draw UI buttons using screen coordinates
        drawUIButtons()
    }

    private fun drawUIButtons() {
        // Use identity matrix for screen coordinates
        game.shapeRenderer.projectionMatrix = com.badlogic.gdx.math.Matrix4().setToOrtho2D(
            0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
        )

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        // Draw thrust button
        inputHandler.thrustButton.render(game.shapeRenderer)
        // Draw joystick
        inputHandler.joystick.render(game.shapeRenderer)
        game.shapeRenderer.end()

        // Draw filled circle when pressed
        if (inputHandler.thrustButton.isPressed) {
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            val btn = inputHandler.thrustButton
            game.shapeRenderer.color = btn.color.cpy().apply { a = 0.3f }
            game.shapeRenderer.circle(btn.position.x, btn.position.y, btn.radius * 0.7f, 40)
            game.shapeRenderer.end()
        }

        // Draw label on button
        game.batch.projectionMatrix = com.badlogic.gdx.math.Matrix4().setToOrtho2D(
            0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
        )
        game.batch.begin()
        val font = game.assets.getFont()
        font.color = inputHandler.thrustButton.color
        font.data.setScale(2.5f)
        val btn = inputHandler.thrustButton
        font.draw(
            game.batch,
            btn.label,
            btn.position.x - 20f,
            btn.position.y + 15f
        )
        game.batch.end()
    }


    private fun grantXp(amount: Int) {
        // Magnet bonus
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

        // Apply stats
        when (def.id) {
            UpgradeId.DAMAGE -> playerStats.baseDamage *= 1.2f // +20%
            UpgradeId.FIRE_RATE -> playerStats.fireCooldown *= 0.88f // -12%
            UpgradeId.BULLET_SPEED -> playerStats.bulletSpeed *= 1.25f // +25%
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
        }

        isPaused = false
    }

    private fun drawLevelUpOverlay() {
        Gdx.gl.glEnable(GL20.GL_BLEND)

        // Draw overlay and card backgrounds
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        game.shapeRenderer.color = com.badlogic.gdx.graphics.Color(0f, 0f, 0f, 0.9f)
        game.shapeRenderer.rect(0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        val totalWidth = offeredUpgrades.size * cardWidth + (offeredUpgrades.size - 1) * cardSpacing
        val startX = (Constants.WORLD_WIDTH - totalWidth) / 2f
        val centerY = Constants.WORLD_HEIGHT / 2f

        game.shapeRenderer.color = com.badlogic.gdx.graphics.Color.DARK_GRAY
        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (cardWidth + cardSpacing)
            val y = centerY - cardHeight / 2f
            game.shapeRenderer.rect(x, y, cardWidth, cardHeight)
        }
        game.shapeRenderer.end()

        // Draw card borders
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = com.badlogic.gdx.graphics.Color.WHITE
        for ((i, _) in offeredUpgrades.withIndex()) {
            val x = startX + i * (cardWidth + cardSpacing)
            val y = centerY - cardHeight / 2f
            game.shapeRenderer.rect(x, y, cardWidth, cardHeight)
        }
        game.shapeRenderer.end()

        // 4. Draw Text
        game.batch.begin()
        val font = game.assets.getFont()
        font.color = com.badlogic.gdx.graphics.Color.WHITE

        // Title - Positioned above cards
        val cardTop = centerY + cardHeight / 2f
        font.data.setScale(3.0f)
        font.draw(game.batch, "LEVEL UP!", 0f, cardTop + 80f, Constants.WORLD_WIDTH, com.badlogic.gdx.utils.Align.center, false)

        for ((i, upgrade) in offeredUpgrades.withIndex()) {
            val x = startX + i * (cardWidth + cardSpacing)

            // Upgrade name
            font.data.setScale(1.8f)
            font.draw(game.batch, upgrade.displayName, x, cardTop - 60f, cardWidth, com.badlogic.gdx.utils.Align.center, false)

            // Level indicator
            val nextLevel = (playerStats.upgradeLevels[upgrade.id] ?: 0) + 1
            font.data.setScale(1.2f)
            font.draw(game.batch, "Level $nextLevel", x, cardTop - 130f, cardWidth, com.badlogic.gdx.utils.Align.center, false)

            // Description
            val desc = upgrade.descriptionPerLevel.getOrNull(nextLevel - 1) ?: ""
            font.data.setScale(1.2f)
            font.draw(game.batch, desc, x + 20f, cardTop - 200f, cardWidth - 40f, com.badlogic.gdx.utils.Align.center, true)
        }
        game.batch.end()
    }

    private fun handleUpgradeInput() {
        if (Gdx.input.justTouched()) {
            val touchX = Gdx.input.x.toFloat()
            val touchY = Gdx.input.y.toFloat()

            // Convert screen to world
            val worldPos = camera.unproject(com.badlogic.gdx.math.Vector3(touchX, touchY, 0f))

            val startX = (Constants.WORLD_WIDTH - (offeredUpgrades.size * cardWidth + (offeredUpgrades.size - 1) * cardSpacing)) / 2f
            val centerY = Constants.WORLD_HEIGHT / 2f
            val cardY = centerY - cardHeight / 2f

            for ((i, upgrade) in offeredUpgrades.withIndex()) {
                val x = startX + i * (cardWidth + cardSpacing)
                if (worldPos.x >= x && worldPos.x <= x + cardWidth &&
                    worldPos.y >= cardY && worldPos.y <= cardY + cardHeight) {
                    applyUpgrade(upgrade)
                    break
                }
            }
        }
    }
}
