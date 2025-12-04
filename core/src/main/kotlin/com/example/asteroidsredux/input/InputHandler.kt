package com.example.asteroidsredux.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.Color
import com.example.asteroidsredux.utils.Constants

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
        position = Vector2(screenWidth * Constants.UI.BUTTON_X_PCT, screenHeight * Constants.UI.BUTTON_Y_PCT),
        radius = Constants.UI.BUTTON_RADIUS,
        color = Color.CYAN,
        label = "↑"
    )

    val joystick = Joystick(
        defaultPosition = Vector2(screenWidth * Constants.UI.JOYSTICK_X_PCT, screenHeight * Constants.UI.JOYSTICK_Y_PCT),
        baseRadius = Constants.UI.JOYSTICK_RADIUS,
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

    // Returns the target angle from the joystick, or null if not active
    val targetAngle: Float?
        get() = joystick.angle

    companion object {
        private const val autoshoot = true
    }
}
