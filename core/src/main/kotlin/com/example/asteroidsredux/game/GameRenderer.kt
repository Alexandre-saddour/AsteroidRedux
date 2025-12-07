package com.example.asteroidsredux.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.example.asteroidsredux.skins.AsteroidSkinId
import com.example.asteroidsredux.skins.ShipSkinId
import com.example.asteroidsredux.skins.SkinManager
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class GameRenderer(
    private val world: WorldManager,
    private val batch: SpriteBatch,
    private val shapeRenderer: ShapeRenderer,
    private val assets: Assets,
    private val skinManager: SkinManager
) {
    private val camera = OrthographicCamera()
    var zoomFactor = Constants.Rendering.DEFAULT_ZOOM
    
    // When true, camera stays centered on screen instead of following ship
    var isIntroMode = false

    init {
        camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
    }

    fun resize(width: Float, height: Float) {
        camera.setToOrtho(false, width, height)
    }

    fun render() {
        // Screen is cleared by BaseScreen

        // Update camera - either follow ship or stay centered (intro mode)
        if (isIntroMode) {
            camera.position.set(Constants.WORLD_WIDTH / 2, Constants.WORLD_HEIGHT / 2, 0f)
        } else {
            camera.position.set(world.ship.position.x, world.ship.position.y, 0f)
        }
        camera.zoom = zoomFactor
        camera.update()

        // Render World (9 times for wrapping)
        val w = Constants.WORLD_WIDTH
        val h = Constants.WORLD_HEIGHT

        for (xOffset in -1..1) {
            for (yOffset in -1..1) {
                // In intro mode, only render the center world (no wrapping)
                // This prevents "ghost" asteroids from appearing on the opposite side
                // while sliding in from off-screen
                if (isIntroMode && (xOffset != 0 || yOffset != 0)) continue

                val offsetX = xOffset * w
                val offsetY = yOffset * h
                
                val combined = camera.combined.cpy()
                combined.translate(offsetX, offsetY, 0f)
                
                shapeRenderer.projectionMatrix = combined
                batch.projectionMatrix = combined
                
                renderWorld()
            }
        }
    }

    private fun renderWorld() {
        val asteroidSkinId = skinManager.selectedAsteroidSkinId
        val shipSkinId = skinManager.selectedShipSkinId
        
        // Render Classic (Line-based) entities
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        if (asteroidSkinId == AsteroidSkinId.CLASSIC) {
            for (asteroid in world.asteroids) asteroid.render(shapeRenderer)
        }
        shapeRenderer.end()
        
        // Render Classic (Filled) entities - Ship uses filled triangle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        if (shipSkinId == ShipSkinId.CLASSIC) {
            world.ship.render(shapeRenderer)
        }
        shapeRenderer.end()

        // Render Sprite-based entities
        batch.begin()
        
        // Asteroids (Sprite)
        if (asteroidSkinId != AsteroidSkinId.CLASSIC) {
            val textureRegion = assets.getAsteroidTexture(asteroidSkinId)
            if (textureRegion != null) {
                for (asteroid in world.asteroids) asteroid.render(batch, textureRegion)
            }
        }
        
        // Ship (Sprite)
        world.ship.renderThrust(batch)
        if (shipSkinId != ShipSkinId.CLASSIC) {
            world.ship.render(batch)
        }
        
        batch.end()

        // Render Bullets and Particles
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        for (bullet in world.bullets) bullet.render(shapeRenderer)
        for (particle in world.particles) particle.render(shapeRenderer)
        shapeRenderer.end()
    }
}
