package com.example.asteroidsredux.skins

enum class ShipSkinId {
    DEFAULT,
    // Add more skins here as they are created
    // PHANTOM,
    // NOVA,
    // STEALTH,
}

data class ShipSkin(
    val id: ShipSkinId,
    val displayName: String,
    val textureFileName: String,
    val unlockCondition: String? = null // null means unlocked by default
)
