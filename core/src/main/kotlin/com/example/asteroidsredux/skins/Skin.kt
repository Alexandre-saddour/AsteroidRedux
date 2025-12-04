package com.example.asteroidsredux.skins

/**
 * Base interface for all skin types.
 */
interface Skin {
    val id: String
    val displayName: String
    val textureFileName: String
    val unlockCondition: String?
}

/**
 * Registry for skins of a specific category.
 * Each catalog (ShipSkinCatalog, AsteroidSkinCatalog, etc.) implements this.
 */
interface SkinRegistry<T : Skin> {
    val skins: List<T>
    fun getSkin(id: String): T
}

/**
 * Categories of skins available in the game.
 * Each category has an associated registry that provides its skins.
 */
enum class SkinCategory {
    SHIP,
    ASTEROID;
    // BULLET,
    // BACKGROUND;
    
    val registry: SkinRegistry<out Skin>
        get() = when (this) {
            SHIP -> ShipSkinCatalog
            ASTEROID -> AsteroidSkinCatalog
        }
}
