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

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        assets = Assets()
        skinManager = SkinManager()
        
        assets.load()
        assets.finishLoading()

        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        assets.dispose()
        super.dispose()
    }
}
