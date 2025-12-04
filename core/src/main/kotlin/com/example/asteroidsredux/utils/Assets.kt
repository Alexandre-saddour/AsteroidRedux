package com.example.asteroidsredux.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Disposable

class Assets : Disposable {
    val manager = AssetManager()

    fun load() {
        // manager.load("fonts/default.fnt", BitmapFont::class.java) // Use default font
        manager.load("sounds/shoot.wav", Sound::class.java)
        manager.load("sounds/explosion.wav", Sound::class.java)
        manager.load("images/ship.png", com.badlogic.gdx.graphics.Texture::class.java)
    }

    fun finishLoading() {
        manager.finishLoading()
    }

    fun getFont(): BitmapFont = BitmapFont() // Use default LibGDX font
    fun getShootSound(): Sound = manager.get("sounds/shoot.wav", Sound::class.java)
    fun getExplosionSound(): Sound = manager.get("sounds/explosion.wav", Sound::class.java)
    fun getShipTexture(): com.badlogic.gdx.graphics.Texture = manager.get("images/ship.png", com.badlogic.gdx.graphics.Texture::class.java)

    override fun dispose() {
        manager.dispose()
    }
}
