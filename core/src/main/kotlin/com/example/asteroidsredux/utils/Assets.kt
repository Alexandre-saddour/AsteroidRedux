package com.example.asteroidsredux.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.skins.AsteroidSkinCatalog
import com.example.asteroidsredux.skins.AsteroidSkinId
import com.example.asteroidsredux.skins.Skin
import com.example.asteroidsredux.skins.ShipSkinCatalog
import com.example.asteroidsredux.skins.ShipSkinId

class Assets : Disposable {
    val manager = AssetManager()
    private lateinit var atlas: TextureAtlas

    fun load() {
        manager.load("sounds/shoot.wav", Sound::class.java)
        manager.load("sounds/explosion.wav", Sound::class.java)
        manager.load("sprites/atlas.atlas", TextureAtlas::class.java)
        
        // UI Textures
        manager.load("ui/title_logo.png", Texture::class.java)
        manager.load("ui/ui_button_default.png", Texture::class.java)
        manager.load("ui/ui_button_pressed.png", Texture::class.java)
        manager.load("ui/bg_stars.png", Texture::class.java)
        manager.load("ui/ui_card.png", Texture::class.java)
        manager.load("ui/ui_card_selected.png", Texture::class.java)
    }

    fun finishLoading() {
        manager.finishLoading()
        atlas = manager.get("sprites/atlas.atlas", TextureAtlas::class.java)
    }

    fun getFont(): BitmapFont = BitmapFont()
    fun getShootSound(): Sound = manager.get("sounds/shoot.wav", Sound::class.java)
    fun getExplosionSound(): Sound = manager.get("sounds/explosion.wav", Sound::class.java)

    // Generic texture region retrieval for any skin
    fun getTextureRegion(skin: Skin): TextureRegion? {
        if (skin.atlasRegionName.isEmpty()) return null
        return atlas.findRegion(skin.atlasRegionName)
    }

    // Type-specific convenience methods
    fun getShipTexture(skinId: ShipSkinId): TextureRegion? = getTextureRegion(ShipSkinCatalog.getSkin(skinId))
    fun getAsteroidTexture(skinId: AsteroidSkinId): TextureRegion? = getTextureRegion(AsteroidSkinCatalog.getSkin(skinId))
    fun getShipTexture(): TextureRegion? = getShipTexture(ShipSkinId.SHIP_1)

    fun getThrustAnimationRegions(skinId: ShipSkinId): Array<TextureAtlas.AtlasRegion>? {
        val skin = ShipSkinCatalog.getSkin(skinId)
        if (skin.thrustAnimationRegionPrefix.isNullOrEmpty()) return null
        return atlas.findRegions(skin.thrustAnimationRegionPrefix)
    }

    // UI Assets
    fun getTitleLogo(): Texture = manager.get("ui/title_logo.png", Texture::class.java)
    fun getButtonDefault(): Texture = manager.get("ui/ui_button_default.png", Texture::class.java)
    fun getButtonPressed(): Texture = manager.get("ui/ui_button_pressed.png", Texture::class.java)
    fun getBackgroundStars(): Texture = manager.get("ui/bg_stars.png", Texture::class.java)
    fun getCard(): Texture = manager.get("ui/ui_card.png", Texture::class.java)
    fun getCardSelected(): Texture = manager.get("ui/ui_card_selected.png", Texture::class.java)

    override fun dispose() {
        manager.dispose()
    }
}
