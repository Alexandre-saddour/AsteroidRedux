package com.example.asteroidsredux.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.skins.ShipSkinCatalog
import com.example.asteroidsredux.skins.ShipSkinId

class Assets : Disposable {
    val manager = AssetManager()

    fun load() {
        manager.load("sounds/shoot.wav", Sound::class.java)
        manager.load("sounds/explosion.wav", Sound::class.java)
        
        // Load all ship skin textures from the catalog
        ShipSkinCatalog.skins.forEach { skin ->
            manager.load(skin.textureFileName, Texture::class.java)
        }
    }

    fun finishLoading() {
        manager.finishLoading()
    }

    fun getFont(): BitmapFont = BitmapFont()
    fun getShootSound(): Sound = manager.get("sounds/shoot.wav", Sound::class.java)
    fun getExplosionSound(): Sound = manager.get("sounds/explosion.wav", Sound::class.java)
    
    // Get texture for a specific skin ID
    fun getShipTexture(skinId: ShipSkinId): Texture {
        val skin = ShipSkinCatalog.getSkin(skinId)
        return manager.get(skin.textureFileName, Texture::class.java)
    }
    
    // Convenience method for backward compatibility (uses default skin)
    fun getShipTexture(): Texture = getShipTexture(ShipSkinId.DEFAULT)

    override fun dispose() {
        manager.dispose()
    }
}
