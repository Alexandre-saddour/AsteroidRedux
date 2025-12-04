package com.example.asteroidsredux.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.skins.AsteroidSkinCatalog
import com.example.asteroidsredux.skins.AsteroidSkinId
import com.example.asteroidsredux.skins.Skin
import com.example.asteroidsredux.skins.SkinCategory
import com.example.asteroidsredux.skins.ShipSkinCatalog
import com.example.asteroidsredux.skins.ShipSkinId

class Assets : Disposable {
    val manager = AssetManager()

    fun load() {
        manager.load("sounds/shoot.wav", Sound::class.java)
        manager.load("sounds/explosion.wav", Sound::class.java)
        
        // Load all skin textures across all categories
        loadAllSkinTextures()
    }
    
    private fun loadAllSkinTextures() {
        val allSkins: List<Skin> = SkinCategory.values().flatMap { category ->
            when (category) {
                SkinCategory.SHIP -> ShipSkinCatalog.skins
                SkinCategory.ASTEROID -> AsteroidSkinCatalog.skins
            }
        }
        
        allSkins.forEach { skin ->
            if (skin.textureFileName.isNotEmpty()) {
                manager.load(skin.textureFileName, Texture::class.java)
            }
        }
    }

    fun finishLoading() {
        manager.finishLoading()
    }

    fun getFont(): BitmapFont = BitmapFont()
    fun getShootSound(): Sound = manager.get("sounds/shoot.wav", Sound::class.java)
    fun getExplosionSound(): Sound = manager.get("sounds/explosion.wav", Sound::class.java)
    
    // Generic texture retrieval for any skin
    fun getTexture(skin: Skin): Texture? {
        if (skin.textureFileName.isEmpty()) return null
        return manager.get(skin.textureFileName, Texture::class.java)
    }
    
    // Type-specific convenience methods
    fun getShipTexture(skinId: ShipSkinId): Texture? = getTexture(ShipSkinCatalog.getSkin(skinId))
    fun getAsteroidTexture(skinId: AsteroidSkinId): Texture? = getTexture(AsteroidSkinCatalog.getSkin(skinId))
    fun getShipTexture(): Texture? = getShipTexture(ShipSkinId.DEFAULT)

    override fun dispose() {
        manager.dispose()
    }
}

