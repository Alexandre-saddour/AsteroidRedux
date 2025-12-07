package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets

class LightOverlayRenderer : Disposable {
    private val haloTexture: Texture
    private val cookieTexture: Texture
    
    init {
        // Load textures. Assuming they are in assets/images/
        // We should ideally use Assets manager, but for now direct load or via Assets if added.
        // Let's assume we load them directly here for simplicity of integration, 
        // or better, add them to Assets.kt. 
        // Given the prompt asked for "Assets minimaux", let's load them.
        haloTexture = Texture("images/halo.png")
        cookieTexture = Texture("images/light_cookie.png")
    }

    fun update() {
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera) {
        // Render a subtle vignette/halo around the player (center of screen)
        // Additive blending
        val originalBlendSrc = batch.blendSrcFunc
        val originalBlendDst = batch.blendDstFunc
        
        batch.setBlendFunction(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE)
        
        val camX = camera.position.x
        val camY = camera.position.y
        val viewW = camera.viewportWidth * camera.zoom
        val viewH = camera.viewportHeight * camera.zoom
        
        // Draw halo at center (player position usually)
        val haloSize = viewH * 0.8f
        batch.setColor(0.1f, 0.3f, 0.5f, 0.3f) // Cold blue halo
        batch.draw(haloTexture, camX - haloSize/2, camY - haloSize/2, haloSize, haloSize)
        
        // Draw light cookie for texture
        batch.setColor(0.05f, 0.05f, 0.1f, 0.1f)
        batch.draw(cookieTexture, camX - viewW/2, camY - viewH/2, viewW, viewH)
        
        batch.setColor(1f, 1f, 1f, 1f)
        batch.setBlendFunction(originalBlendSrc, originalBlendDst)
    }

    override fun dispose() {
        haloTexture.dispose()
        cookieTexture.dispose()
    }
}
