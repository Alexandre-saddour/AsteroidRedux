package com.example.asteroidsredux.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Interpolation
import com.example.asteroidsredux.AsteroidsGame

enum class TransitionType {
    SLIDE_LEFT,  // New screen slides in from right, old slides out to left
    SLIDE_RIGHT, // New screen slides in from left, old slides out to right
    FADE,        // Cross-fade
    MENU_TO_CUSTOMIZE, // Fade out Menu, Slide Up Customize
    CUSTOMIZE_TO_MENU  // Slide Down Customize, Fade In Menu
}

class TransitionScreen(
    game: AsteroidsGame,
    private val fromScreen: Screen,
    private val toScreen: Screen,
    private val type: TransitionType = TransitionType.SLIDE_LEFT,
    private val duration: Float = 0.8f
) : BaseScreen(game) {

    private var time = 0f
    private var fromFbo: FrameBuffer? = null
    private var fromTexture: com.badlogic.gdx.graphics.g2d.TextureRegion? = null
    private var toFbo: FrameBuffer? = null
    private var disposed = false
    private var toRegion: com.badlogic.gdx.graphics.g2d.TextureRegion? = null

    private val uiCamera = com.badlogic.gdx.graphics.OrthographicCamera()

    override fun show() {
        Gdx.app.log("TransitionScreen", "show() called. Type: $type")

        val w = Gdx.graphics.width
        val h = Gdx.graphics.height

        uiCamera.setToOrtho(false, w.toFloat(), h.toFloat())

        createFbos(w, h)

        // Prepare 'toScreen' for rendering (resize only, don't call show() to avoid double init)
        toScreen.resize(w, h)

        captureScreens()
    }

    private fun createFbos(w: Int, h: Int) {
        try {
            // Dispose old FBOs if they exist (e.g. on resize)
            // Note: We don't set disposed=true here because we are recreating them.
            fromFbo?.dispose()
            toFbo?.dispose()

            // Use hasDepth = false for 2D
            fromFbo = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)
            toFbo = FrameBuffer(Pixmap.Format.RGBA8888, w, h, false)

            fromFbo?.colorBufferTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            toFbo?.colorBufferTexture?.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

            // Cache toRegion and flip it once
            toFbo?.colorBufferTexture?.let {
                toRegion = com.badlogic.gdx.graphics.g2d.TextureRegion(it)
                toRegion?.flip(false, true)
            }
        } catch (e: Exception) {
            Gdx.app.error("TransitionScreen", "Failed to create FBOs", e)
            game.screen = toScreen
        }
    }

    private fun captureScreens() {
        if (fromFbo == null || toFbo == null) return

        // Capture 'fromScreen'
        fromFbo?.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (fromScreen is BaseScreen) {
            fromScreen.renderUiOnly(0f)
        } else {
            fromScreen.render(0f)
        }
        fromFbo?.end()

        // Capture 'toScreen'
        toFbo?.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        // We render with a small delta or 0. If animations depend on time, 0 might be static.
        if (toScreen is BaseScreen) {
            toScreen.renderUiOnly(0f)
        } else {
            toScreen.render(0f)
        }
        toFbo?.end()

        // Create TextureRegions and flip them once
        fromFbo?.colorBufferTexture?.let {
            fromTexture = com.badlogic.gdx.graphics.g2d.TextureRegion(it)
            fromTexture?.flip(false, true)
        }
    }

    override fun drawUi(delta: Float) {
        // Not used
    }

    override fun render(delta: Float) {
        if (fromFbo == null || toFbo == null || duration <= 0f) {
            game.screen = toScreen
            return
        }

        time += delta
        val alpha = (time / duration).coerceIn(0f, 1f)
        val interpolated = Interpolation.exp10Out.apply(alpha)

        // Render 'toScreen' UI ONLY into FBO
        // We do this every frame to support dynamic UI in the target screen
        toFbo?.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (toScreen is BaseScreen) {
            toScreen.renderUiOnly(delta)
        } else {
            toScreen.render(delta)
        }
        toFbo?.end()

        // 1. Draw Shared Background
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        game.backgroundRenderer.update(delta)
        game.backgroundRenderer.draw(game.batch)

        // 2. Draw Transitions
        val w = Gdx.graphics.width.toFloat()
        val h = Gdx.graphics.height.toFloat()

        game.batch.projectionMatrix = uiCamera.combined
        if (!game.batch.isBlendingEnabled) game.batch.enableBlending()
        game.batch.begin()

        when (type) {
            TransitionType.SLIDE_LEFT -> {
                val offset = w * interpolated
                fromTexture?.let { game.batch.draw(it, -offset, 0f, w, h) }
                toRegion?.let { game.batch.draw(it, w - offset, 0f, w, h) }
            }
            TransitionType.SLIDE_RIGHT -> {
                val offset = w * interpolated
                fromTexture?.let { game.batch.draw(it, offset, 0f, w, h) }
                toRegion?.let { game.batch.draw(it, -w + offset, 0f, w, h) }
            }
            TransitionType.FADE -> {
                fromTexture?.let {
                    game.batch.setColor(1f, 1f, 1f, 1f)
                    game.batch.draw(it, 0f, 0f, w, h)
                }
                toRegion?.let {
                    game.batch.setColor(1f, 1f, 1f, interpolated)
                    game.batch.draw(it, 0f, 0f, w, h)
                }
            }
            TransitionType.MENU_TO_CUSTOMIZE -> {
                fromTexture?.let {
                    game.batch.setColor(1f, 1f, 1f, 1f - interpolated)
                    game.batch.draw(it, 0f, 0f, w, h)
                }
                val yOffset = -h + (h * interpolated)
                toRegion?.let {
                    game.batch.setColor(1f, 1f, 1f, 1f)
                    game.batch.draw(it, 0f, yOffset, w, h)
                }
            }
            TransitionType.CUSTOMIZE_TO_MENU -> {
                // Menu (toRegion) Fades In
                toRegion?.let {
                    game.batch.setColor(1f, 1f, 1f, interpolated)
                    game.batch.draw(it, 0f, 0f, w, h)
                }

                // Customize (fromTexture) Slides Down
                // Starts at 0, moves to -h
                val yOffset = -(h * interpolated)
                fromTexture?.let {
                    game.batch.setColor(1f, 1f, 1f, 1f)
                    game.batch.draw(it, 0f, yOffset, w, h)
                }
            }
        }

        game.batch.setColor(1f, 1f, 1f, 1f)
        game.batch.end()

        if (time >= duration) {
            fromScreen.dispose()
            if (!disposed) dispose()
            game.screen = toScreen
        }
    }

    override fun resize(width: Int, height: Int) {
        uiCamera.setToOrtho(false, width.toFloat(), height.toFloat())
        createFbos(width, height)
        captureScreens() // Re-capture static fromScreen

        fromScreen.resize(width, height)
        toScreen.resize(width, height)
    }

    override fun hide() {
        if (!disposed) dispose()
    }

    override fun dispose() {
        if (disposed) return
        disposed = true

        fromTexture = null
        toRegion = null

        fromFbo?.dispose()
        fromFbo = null

        toFbo?.dispose()
        toFbo = null
    }
}
