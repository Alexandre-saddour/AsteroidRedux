package com.example.asteroidsredux.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Pixmap
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

    // 1×1 white pixel texture for batched drawing (stars, debris)
    private lateinit var pixelTexture: Texture
    private lateinit var pixelRegion: TextureRegion

    fun load() {
        manager.load("sounds/shoot.wav", Sound::class.java)
        manager.load("sounds/explosion.wav", Sound::class.java)
        manager.load("sprites/atlas.atlas", TextureAtlas::class.java)
        manager.load("images/halo.png", Texture::class.java)
        manager.load("images/light_cookie.png", Texture::class.java)
    }

    private lateinit var font: BitmapFont

    fun finishLoading() {
        manager.finishLoading()
        atlas = manager.get("sprites/atlas.atlas", TextureAtlas::class.java)
        font = BitmapFont()
        shaderManager.load()

        // Create 1×1 white pixel texture for efficient batched rendering
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        pixelTexture = Texture(pixmap)
        pixmap.dispose()
        pixelRegion = TextureRegion(pixelTexture)
    }

    fun getFont(): BitmapFont = font
    fun getShootSound(): Sound = manager.get("sounds/shoot.wav", Sound::class.java)
    fun getExplosionSound(): Sound = manager.get("sounds/explosion.wav", Sound::class.java)

    // === Background System Assets ===
    /** 1×1 white pixel for batched star/debris rendering */
    fun getPixelRegion(): TextureRegion = pixelRegion

    /** Halo texture for additive light overlays */
    fun getHaloTexture(): Texture = manager.get("images/halo.png", Texture::class.java)

    /** Light cookie texture for atmospheric overlays */
    fun getLightCookieTexture(): Texture = manager.get("images/light_cookie.png", Texture::class.java)

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

    // UI Assets (Retrieved from Atlas)
    fun getTitleLogo(): TextureRegion = atlas.findRegion("ui/title_logo")
    fun getButtonDefault(): TextureRegion = atlas.findRegion("ui/ui_button_default")
    fun getButtonPressed(): TextureRegion = atlas.findRegion("ui/ui_button_pressed")
    fun getBackgroundStars(): TextureRegion = atlas.findRegion("bg_stars")
    fun getCard(): TextureRegion = atlas.findRegion("ui/ui_card")
    fun getCardSelected(): TextureRegion = atlas.findRegion("ui/ui_card_selected")

    val shaderManager = ShaderManager()

    override fun dispose() {
        manager.dispose()
        pixelTexture.dispose()
        shaderManager.dispose()
    }
}

