package com.example.asteroidsredux.entities

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.example.asteroidsredux.input.InputHandler
import com.example.asteroidsredux.progression.PlayerStats
import com.example.asteroidsredux.skins.ShipSkinId
import com.example.asteroidsredux.skins.SkinManager
import com.example.asteroidsredux.utils.Assets
import com.example.asteroidsredux.utils.Constants
import com.example.asteroidsredux.utils.MathUtils2D
import com.example.asteroidsredux.utils.SpriteRenderer

class Ship(
    private val inputHandler: InputHandler,
    private val stats: PlayerStats,
    private val assets: Assets,
    val skinManager: SkinManager
) {
    val position = Vector2(Constants.WORLD_WIDTH / 2, Constants.WORLD_HEIGHT / 2)
    val velocity = Vector2()
    var angle = MathUtils.PI / 2 // Pointing up
    val nose = Vector2()
    var isDead = false

    // Current texture region (null for classic skin)
    private var textureRegion: TextureRegion? = assets.getShipTexture(skinManager.selectedShipSkinId)
    private var thrustAnimation: Animation<TextureRegion>? = null
    private var stateTime = 0f

    init {
        // Listen for skin changes
        skinManager.addShipSkinChangeListener { skinId ->
            textureRegion = assets.getShipTexture(skinId)
            loadThrustAnimation(skinId)
        }
        loadThrustAnimation(skinManager.selectedShipSkinId)
    }

    // Manual skin switching (if needed without going through SkinManager)
    fun setSkin(skinId: ShipSkinId) {
        textureRegion = assets.getShipTexture(skinId)
        loadThrustAnimation(skinId)
    }

    private fun loadThrustAnimation(skinId: ShipSkinId) {
        val thrustRegions = assets.getThrustAnimationRegions(skinId)
        if (thrustRegions != null && thrustRegions.size > 0) {
            thrustAnimation = Animation(0.1f, thrustRegions, Animation.PlayMode.LOOP)
        } else {
            thrustAnimation = null
        }
    }

    fun update(delta: Float) {
        if (isDead) return

        // Rotation - rotate towards joystick direction if active
        val targetAngle = inputHandler.targetAngle
        if (targetAngle != null) {
            // Calculate shortest rotation direction
            var angleDiff = targetAngle - angle

            // Normalize to [-PI, PI]
            while (angleDiff > MathUtils.PI) angleDiff -= MathUtils.PI2
            while (angleDiff < -MathUtils.PI) angleDiff += MathUtils.PI2

            // Rotate towards target at rotation speed
            val maxRotation = Constants.SHIP_ROTATION_SPEED * delta
            if (kotlin.math.abs(angleDiff) < maxRotation) {
                angle = targetAngle
            } else if (angleDiff > 0) {
                angle += maxRotation
            } else {
                angle -= maxRotation
            }
        }

        // Thrust
        if (inputHandler.isThrusting) {
            val thrust = Vector2(stats.shipAcceleration * delta, 0f).rotateRad(angle)
            velocity.add(thrust)
        } else {
            // Apply deceleration/friction when not thrusting
            val deceleration = 0.99f
            velocity.scl(deceleration)

            if (velocity.len() < 1f) {
                velocity.set(0f, 0f)
            }
        }

        // Cap speed
        if (velocity.len() > stats.shipMaxSpeed) {
            velocity.setLength(stats.shipMaxSpeed)
        }

        // Move
        position.mulAdd(velocity, delta)
        MathUtils2D.wrapAround(position, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT)

        // Update nose position for shooting
        nose.set(Constants.SHIP_SIZE, 0f).rotateRad(angle).add(position)

        stateTime += delta
    }

    // Classic (polygon) rendering
    fun render(shapeRenderer: ShapeRenderer) {
        if (isDead) return

        shapeRenderer.color = Constants.SHIP_COLOR

        // Triangle ship pointing in direction of angle
        val size = Constants.SHIP_SIZE
        val x = position.x
        val y = position.y

        // Calculate triangle vertices
        val tipX = x + MathUtils.cos(angle) * size
        val tipY = y + MathUtils.sin(angle) * size
        val leftX = x + MathUtils.cos(angle + MathUtils.PI * 0.8f) * size * 0.7f
        val leftY = y + MathUtils.sin(angle + MathUtils.PI * 0.8f) * size * 0.7f
        val rightX = x + MathUtils.cos(angle - MathUtils.PI * 0.8f) * size * 0.7f
        val rightY = y + MathUtils.sin(angle - MathUtils.PI * 0.8f) * size * 0.7f

        shapeRenderer.triangle(tipX, tipY, leftX, leftY, rightX, rightY)
    }

    // Sprite rendering
    fun render(batch: SpriteBatch) {
        if (isDead) return

        val region = textureRegion ?: return // Can't render sprite without texture

        val degrees = (angle * MathUtils.radiansToDegrees) - 90f

        // Scale up if using a skin
        val scale = if (skinManager.selectedShipSkinId != ShipSkinId.CLASSIC) Constants.SHIP_SKIN_SCALE else 1.0f
        val size = Constants.SHIP_SIZE * scale

        SpriteRenderer.drawCentered(batch, region, position.x, position.y, size, degrees)
    }

    fun renderGlow(batch: SpriteBatch) {
        if (isDead) return
        if (skinManager.selectedShipSkinId == ShipSkinId.CLASSIC) return // No glow for classic skin

        val region = textureRegion ?: return
        val degrees = (angle * MathUtils.radiansToDegrees) - 90f

        // Glow is larger than the ship
        val scale = Constants.SHIP_SKIN_SCALE * Constants.SHIP_GLOW_SIZE_MULT
        val size = Constants.SHIP_SIZE * scale

        val shader = assets.shaderManager.get("glow")
        val originalShader = batch.shader
        batch.shader = shader

        shader.setUniformf("u_glowColor", Constants.SHIP_GLOW_COLOR)

        SpriteRenderer.drawCentered(batch, region, position.x, position.y, size, degrees)

        batch.shader = originalShader
    }

    fun renderThrust(batch: SpriteBatch) {
        if (isDead) return

        // Render thrust if thrusting
        if (inputHandler.isThrusting && thrustAnimation != null) {
            val degrees = (angle * MathUtils.radiansToDegrees) + 180f // +180 to point flame behind ship
            val currentFrame = thrustAnimation!!.getKeyFrame(stateTime, true)

            // Position behind the ship
            val fireOffset = Vector2(-Constants.SHIP_SIZE * 1.2f, 0f).rotateRad(angle)
            val fireX = position.x + fireOffset.x
            val fireY = position.y + fireOffset.y

            SpriteRenderer.drawCentered(batch, currentFrame, fireX, fireY, Constants.SHIP_SIZE, degrees)
        }
    }
}
