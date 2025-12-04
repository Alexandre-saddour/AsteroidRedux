package com.example.asteroidsredux.game

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import com.example.asteroidsredux.entities.Asteroid
import com.example.asteroidsredux.entities.Bullet
import com.example.asteroidsredux.entities.Particle
import com.example.asteroidsredux.entities.Ship
import com.example.asteroidsredux.utils.Constants

class WorldManager(val ship: Ship) {
    val asteroids = Array<Asteroid>()
    val bullets = Array<Bullet>()
    val particles = Array<Particle>()

    // Temporary arrays to avoid nested iteration issues
    private val asteroidsToAdd = Array<Asteroid>()
    private val asteroidsToRemove = Array<Asteroid>()

    fun update(delta: Float) {
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

        // Apply deferred asteroid operations
        for (asteroid in asteroidsToRemove) {
            asteroids.removeValue(asteroid, true)
        }
        asteroidsToRemove.clear()

        for (asteroid in asteroidsToAdd) {
            asteroids.add(asteroid)
        }
        asteroidsToAdd.clear()
    }

    fun spawnAsteroids(count: Int) {
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

    fun addAsteroid(asteroid: Asteroid) {
        asteroidsToAdd.add(asteroid)
    }

    fun removeAsteroid(asteroid: Asteroid) {
        asteroidsToRemove.add(asteroid)
    }

    fun addBullet(bullet: Bullet) {
        bullets.add(bullet)
    }

    fun spawnParticles(x: Float, y: Float, count: Int) {
        for (i in 0 until count) {
            val p = Particle()
            val angle = MathUtils.random(0f, MathUtils.PI2)
            val speed = MathUtils.random(1f, Constants.PARTICLE_SPEED)
            p.init(x, y, MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed)
            particles.add(p)
        }
    }
}
