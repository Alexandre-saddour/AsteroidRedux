package com.example.asteroidsredux

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.example.asteroidsredux.screens.MenuScreen
import com.example.asteroidsredux.skins.SkinManager
import com.example.asteroidsredux.utils.Assets

class AsteroidsGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var assets: Assets
    lateinit var skinManager: SkinManager
    lateinit var backgroundRenderer: com.example.asteroidsredux.utils.BackgroundRenderer

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        assets = Assets()
        skinManager = SkinManager()
        assets.load()
        assets.finishLoading()
        
        backgroundRenderer = com.example.asteroidsredux.utils.BackgroundRenderer(this)

        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        assets.dispose()
        super.dispose()
    }

    fun changeScreen(newScreen: com.badlogic.gdx.Screen, type: com.example.asteroidsredux.screens.TransitionType) {
        setScreen(com.example.asteroidsredux.screens.TransitionScreen(this, this.screen, newScreen, type))
    }
}
