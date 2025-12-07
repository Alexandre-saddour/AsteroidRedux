package com.example.asteroidsredux.graphics.background

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants

class NebulaRenderer(assets: Assets) : Disposable {
    private val shader: ShaderProgram
    private val emptyTexture: Texture

    init {
        val vert = Gdx.files.internal("shaders/default.vert")
        val frag = Gdx.files.internal("shaders/nebula.frag")
        shader = ShaderProgram(vert, frag)
        if (!shader.isCompiled) {
            Gdx.app.error("NebulaRenderer", "Shader compilation failed:\n" + shader.log)
        }

        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(1f, 1f, 1f, 1f)
        pixmap.fill()
        emptyTexture = Texture(pixmap)
        pixmap.dispose()
    }

    private var time = 0f
    private var scrollX = 0f
    private var scrollY = 0f

    fun update(delta: Float, cumulativeScrollX: Float, cumulativeScrollY: Float) {
        time += delta
        scrollX = cumulativeScrollX
        scrollY = cumulativeScrollY
    }

    fun render(batch: SpriteBatch, camera: OrthographicCamera) {
        if (!shader.isCompiled) return

        batch.shader = shader
        shader.bind()
        shader.setUniformf("u_time", time)
        shader.setUniformf("u_resolution", Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)
        shader.setUniformf("u_scroll", scrollX, scrollY)

        val x = camera.position.x - camera.viewportWidth * camera.zoom / 2
        val y = camera.position.y - camera.viewportHeight * camera.zoom / 2
        val w = camera.viewportWidth * camera.zoom
        val h = camera.viewportHeight * camera.zoom

        batch.draw(emptyTexture, x, y, w, h)
        batch.shader = null
    }

    override fun dispose() {
        shader.dispose()
        emptyTexture.dispose()
    }
}
