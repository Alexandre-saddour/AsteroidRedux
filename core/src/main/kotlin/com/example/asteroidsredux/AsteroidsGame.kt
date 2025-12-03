package com.example.asteroidsredux

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.example.asteroidsredux.screens.MenuScreen
import com.example.asteroidsredux.utils.Assets

class AsteroidsGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var assets: Assets

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        assets = Assets()
        assets.load()
        assets.finishLoading() // Block until assets are loaded for simplicity

        setScreen(MenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        assets.dispose()
        super.dispose()
    }
}
