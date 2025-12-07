package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class DebrisRenderer : Disposable {
    private val shapeRenderer = ShapeRenderer()
    
    // Debris moves faster than camera (foreground)
    data class Debris(var x: Float, var y: Float, val size: Float, val speed: Float)
    
    private val debrisList = ArrayList<Debris>()
    private val DEBRIS_COUNT = 50
    
    init {
        for (i in 0 until DEBRIS_COUNT) {
            debrisList.add(Debris(
                MathUtils.random(0f, Constants.WORLD_WIDTH),
                MathUtils.random(0f, Constants.WORLD_HEIGHT),
                MathUtils.random(1f, 2f),
                MathUtils.random(1.2f, 1.5f) // > 1.0 for foreground
            ))
        }
    }

    fun update(delta: Float) {
        // Optional: Animate debris drifting
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera) {
        batch.end()
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom
        
        shapeRenderer.setColor(0.6f, 0.7f, 0.8f, 0.5f) // Blue-ish gray, semi-transparent
        
        for (d in debrisList) {
            val parallaxX = (d.x - camX) * d.speed + camX
            val parallaxY = (d.y - camY) * d.speed + camY
            
            var drawX = parallaxX
            var drawY = parallaxY
            
            val halfW = viewW / 2
            val halfH = viewH / 2
             // Wrap relative to camera view (scaled by parallax speed to keep density)
             // Simplified wrapping:
            val rangeW = viewW * d.speed
            val rangeH = viewH * d.speed
            
            // Re-calculate wrapping based on camera position relative to debris world
            // This is a bit tricky for infinite scrolling. 
            // Let's just wrap them in a local box around the camera.
            
            val relX = (drawX - camX) % rangeW
            val relY = (drawY - camY) % rangeH
            
            drawX = camX + relX
            drawY = camY + relY
            
            if (drawX < camX - viewW/2) drawX += rangeW
            if (drawX > camX + viewW/2) drawX -= rangeW
             if (drawY < camY - viewH/2) drawY += rangeH
            if (drawY > camY + viewH/2) drawY -= rangeH

            shapeRenderer.rect(drawX, drawY, d.size, d.size)
        }
        
        shapeRenderer.end()
        batch.begin()
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}
