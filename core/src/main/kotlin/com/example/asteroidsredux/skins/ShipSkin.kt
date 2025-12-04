package com.example.asteroidsredux.skins

enum class ShipSkinId {
    DEFAULT,
    // Add more skins here as they are created
    // PHANTOM,
    // NOVA,
    // STEALTH,
}

data class ShipSkin(
    override val id: String,
    override val displayName: String,
    override val textureFileName: String,
    override val unlockCondition: String? = null
) : Skin {
    constructor(
        id: ShipSkinId,
        displayName: String,
        textureFileName: String,
        unlockCondition: String? = null
    ) : this(id.name, displayName, textureFileName, unlockCondition)
    
    val skinId: ShipSkinId get() = ShipSkinId.valueOf(id)
}
