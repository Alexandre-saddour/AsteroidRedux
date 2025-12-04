package com.example.asteroidsredux.skins

/**
 * Categories of customizable skins in the game.
 */
enum class SkinCategory {
    SHIP,
    ASTEROID,
    // BULLET,
    // BACKGROUND,
}

/**
 * Generic skin interface for all skin types.
 */
interface Skin {
    val id: String
    val displayName: String
    val textureFileName: String
    val unlockCondition: String? // null = unlocked by default
}
