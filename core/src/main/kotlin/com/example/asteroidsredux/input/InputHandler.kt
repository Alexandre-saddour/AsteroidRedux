package com.example.asteroidsredux.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Color

class InputHandler : InputAdapter() {
    var isRotatingLeft = false
    var isRotatingRight = false
    var isThrusting = false
    var isShooting = autoshoot || false

    // For touch controls
    private val screenWidth = Gdx.graphics.width.toFloat()
    private val screenHeight = Gdx.graphics.height.toFloat()

    // UI Controls
    val thrustButton = TouchButton(
        position = Vector2(screenWidth * 0.85f, screenHeight * 0.35f),
        radius = 60f,
        color = Color.CYAN,
        label = "↑"
    )

    val joystick = Joystick(
        defaultPosition = Vector2(screenWidth * 0.15f, screenHeight * 0.35f),
        baseRadius = 80f,
        color = Color.CYAN
    )

    private var thrustPointer = -1

//    override fun keyDown(keycode: Int): Boolean {
//        when (keycode) {
//            Input.Keys.LEFT -> isRotatingLeft = true
//            Input.Keys.RIGHT -> isRotatingRight = true
//            Input.Keys.UP -> isThrusting = true
//            Input.Keys.SPACE -> isShooting = true
//        }
//        return true
//    }
//
//    override fun keyUp(keycode: Int): Boolean {
//        when (keycode) {
//            Input.Keys.LEFT -> isRotatingLeft = false
//            Input.Keys.RIGHT -> isRotatingRight = false
//            Input.Keys.UP -> isThrusting = false
//            Input.Keys.SPACE -> isShooting = false
//        }
//        return true
//    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val touchY = screenHeight - screenY // Flip Y coordinate

        // Check if thrust button is pressed
        if (thrustButton.contains(screenX.toFloat(), touchY)) {
            thrustPointer = pointer
            thrustButton.isPressed = true
            isThrusting = true
            return true
        }

        // Check if joystick area is touched (left half of screen, excluding buttons)
        if (screenX < screenWidth / 2) {
            if (joystick.handleTouchDown(screenX.toFloat(), touchY, pointer)) {
                return true
            }
        }

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val touchY = screenHeight - screenY // Flip Y coordinate
        joystick.handleTouchDragged(screenX.toFloat(), touchY, pointer)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // Check if this was the thrust button pointer
        if (pointer == thrustPointer) {
            thrustButton.isPressed = false
            isThrusting = false
            thrustPointer = -1
            return true
        }

        // Check if joystick was released
        if (joystick.handleTouchUp(pointer)) {
            return true
        }

        if (pointer == 0) {
            isRotatingLeft = false
            isRotatingRight = false
            // Don't set isShooting to false - we want auto-shoot to continue
        } else if (pointer == 1) {
            isThrusting = false
        }
        return true
    }

    // Call this in update loop to apply joystick rotation
    fun updateRotation() {
        val joyRotation = joystick.rotationValue

        // Apply rotation based on joystick position
        if (joyRotation < -0.1f) {
            isRotatingLeft = true
            isRotatingRight = false
        } else if (joyRotation > 0.1f) {
            isRotatingLeft = false
            isRotatingRight = true
        } else {
            // Dead zone - no rotation from joystick
            if (joystick.rotationValue == 0f) {
                // Only reset if joystick is fully centered (not active)
                // This allows keyboard controls to still work
                isRotatingLeft = false
                isRotatingRight = false
            }
        }
    }

    companion object {
        private const val autoshoot = true
    }
}
