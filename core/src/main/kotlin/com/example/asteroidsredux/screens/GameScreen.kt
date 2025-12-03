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

class GameScreen(private val game: AsteroidsGame) : ScreenAdapter() {
    private val camera = OrthographicCamera()
    private val inputHandler = InputHandler()

    private val ship = Ship(inputHandler)
    private val asteroids = Array<Asteroid>()
    private val bullets = Array<Bullet>()
    private val particles = Array<Particle>()

    private var score = 0
    private var level = 1

    init {
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
        update(delta)
        draw()
    }

    private fun update(delta: Float) {
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
            level++
            spawnAsteroids(2 + level)
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
            val bullet = Bullet()
            bullet.init(ship.nose.x, ship.nose.y, ship.angle)
            bullets.add(bullet)
            game.assets.getShootSound().play()
            shootTimer = 0.2f // 5 shots per second
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
                    bullet.active = false
                    asteroid.active = false
                    bulletIter.remove()
                    asteroidIter.remove()

                    handleAsteroidDestruction(asteroid)
                    break
                }
            }
        }

        // Ship <-> Asteroid
        for (asteroid in asteroids) {
            // Simple circle collision for ship
            val dist = ship.position.dst(asteroid.position)
            if (dist < Constants.SHIP_SIZE + asteroid.size) {
                // Boom
                ship.isDead = true
                game.assets.getExplosionSound().play()
                spawnParticles(ship.position.x, ship.position.y, 20)
                break
            }
        }
    }

    private fun handleAsteroidDestruction(asteroid: Asteroid) {
        game.assets.getExplosionSound().play()
        spawnParticles(asteroid.position.x, asteroid.position.y, 10)

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
        font.data.setScale(0.05f)
        font.draw(game.batch, "Score: $score", 2f, Constants.WORLD_HEIGHT - 2f)
        game.batch.end()
    }
}
