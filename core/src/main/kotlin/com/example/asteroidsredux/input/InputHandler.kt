package com.example.asteroidsredux.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter

class InputHandler : InputAdapter() {
    var isRotatingLeft = false
    var isRotatingRight = false
    var isThrusting = false
    var isShooting = autoshoot || false

    // For touch controls
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> isRotatingLeft = true
            Input.Keys.RIGHT -> isRotatingRight = true
            Input.Keys.UP -> isThrusting = true
            Input.Keys.SPACE -> isShooting = true
        }
        return true
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.LEFT -> isRotatingLeft = false
            Input.Keys.RIGHT -> isRotatingRight = false
            Input.Keys.UP -> isThrusting = false
            Input.Keys.SPACE -> isShooting = false
        }
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer == 0) {
            // First finger: Rotation or Shooting
            if (screenX < screenWidth / 2) {
                isRotatingLeft = true
            } else {
                isRotatingRight = true
            }
            // Simple tap for shooting logic can be handled in GameScreen update or here with a timer
            // For now, let's say a quick tap is shoot, hold is rotate.
            // But requirement says: "Tactile : moitié gauche = rotation gauche, moitié droite = rotation droite."
            // "Double-tap ou second doigt = thrust."
            // "Tap court = tir"

            // We'll handle state here, and GameScreen can interpret "just touched" for shooting if needed.
            isShooting = true // Trigger shoot on initial touch down for simplicity, or handle "Tap" specifically
        } else if (pointer == 1) {
            // Second finger: Thrust
            isThrusting = true
        }
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer == 0) {
            isRotatingLeft = false
            isRotatingRight = false
            isShooting = false
        } else if (pointer == 1) {
            isThrusting = false
        }
        return true
    }

    companion object {
        private const val autoshoot = true
    }
}
