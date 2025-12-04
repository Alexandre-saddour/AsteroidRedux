package com.example.asteroidsredux.game

import com.example.asteroidsredux.entities.Asteroid
import com.example.asteroidsredux.progression.PlayerStats
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class CollisionSystem(
    private val world: WorldManager,
    private val playerStats: PlayerStats,
    private val assets: Assets,
    private val onScore: (Int) -> Unit,
    private val onXp: (Int) -> Unit
) {

    fun checkCollisions() {
        if (world.ship.isDead) return

        // Bullet <-> Asteroid
        val bulletIter = world.bullets.iterator()
        while (bulletIter.hasNext()) {
            val bullet = bulletIter.next()
            val asteroidIter = world.asteroids.iterator()
            while (asteroidIter.hasNext()) {
                val asteroid = asteroidIter.next()
                if (asteroid.polygon.contains(bullet.position)) {
                    asteroid.active = false
                    asteroidIter.remove()

                    handleAsteroidDestruction(asteroid)

                    // Pierce mechanic
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
        for (asteroid in world.asteroids) {
            val dist = world.ship.position.dst(asteroid.position)
            if (dist < Constants.SHIP_SIZE + asteroid.size) {
                // Take damage
                playerStats.currentHp--
                assets.getExplosionSound().play()
                world.spawnParticles(world.ship.position.x, world.ship.position.y, 10)

                // Destroy the asteroid that hit us
                asteroid.active = false
                world.removeAsteroid(asteroid) // Using removeAsteroid to defer if needed, but here we are iterating directly?
                // Wait, world.asteroids is an Array. Iterating and removing via removeValue is safe if we use iterator or careful index.
                // But here we are in a for-each loop. Modifying the collection is dangerous.
                // WorldManager exposes the raw array.
                // Let's use world.removeAsteroid which adds to deferred list.
                // But we need to ensure it's removed from current frame logic if we continue.
                // Actually, GameScreen used removeValue(true) inside the loop which is risky if not using iterator.
                // GameScreen used `asteroids.removeValue(asteroid, true)` inside `for (asteroid in asteroids)`. This causes ConcurrentModificationException usually, or index skipping.
                // LibGDX Array iterator might handle it, or it was a bug waiting to happen.
                // Safer to use deferred removal.
                
                // Check if dead
                if (playerStats.currentHp <= 0) {
                    world.ship.isDead = true
                    world.spawnParticles(world.ship.position.x, world.ship.position.y, 30)
                }
                break
            }
        }
    }

    private fun handleAsteroidDestruction(asteroid: Asteroid) {
        assets.getExplosionSound().play()
        world.spawnParticles(asteroid.position.x, asteroid.position.y, 10)

        // Grant XP
        onXp(asteroid.xpValue)

        when (asteroid.size) {
            Constants.ASTEROID_SIZE_LARGE -> {
                onScore(Constants.ASTEROID_SCORE_LARGE)
                world.addAsteroid(Asteroid(Constants.ASTEROID_SIZE_MEDIUM, asteroid.position.x, asteroid.position.y))
                world.addAsteroid(Asteroid(Constants.ASTEROID_SIZE_MEDIUM, asteroid.position.x, asteroid.position.y))
            }
            Constants.ASTEROID_SIZE_MEDIUM -> {
                onScore(Constants.ASTEROID_SCORE_MEDIUM)
                world.addAsteroid(Asteroid(Constants.ASTEROID_SIZE_SMALL, asteroid.position.x, asteroid.position.y))
                world.addAsteroid(Asteroid(Constants.ASTEROID_SIZE_SMALL, asteroid.position.x, asteroid.position.y))
            }
            Constants.ASTEROID_SIZE_SMALL -> {
                onScore(Constants.ASTEROID_SCORE_SMALL)
            }
        }

        // Explosion Radius Upgrade
        if (playerStats.explosionRadiusLevel > 0) {
            val baseRadius = 80f
            val radius = baseRadius * (1f + 0.25f * playerStats.explosionRadiusLevel)

            // Use a snapshot or iterate carefully.
            // WorldManager doesn't expose snapshot.
            // But we can iterate indices.
            for (i in 0 until world.asteroids.size) {
                val nearby = world.asteroids[i]
                if (nearby.active && nearby.position.dst(asteroid.position) <= radius) {
                    if (nearby.size == Constants.ASTEROID_SIZE_SMALL) {
                        nearby.active = false
                        world.removeAsteroid(nearby)
                        handleAsteroidDestruction(nearby) // Recursive
                    }
                }
            }
        }
    }
}
