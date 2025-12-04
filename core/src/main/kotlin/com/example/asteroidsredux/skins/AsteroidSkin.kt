package com.example.asteroidsredux.skins

enum class AsteroidSkinId {
    CLASSIC,
    ROCK,
    // Add more skins here
    // ICE,
    // LAVA,
}

data class AsteroidSkin(
    override val id: String,
    override val displayName: String,
    override val textureFileName: String, // For CLASSIC, this can be ignored or empty
    override val unlockCondition: String? = null
) : Skin {
    constructor(
        id: AsteroidSkinId,
        displayName: String,
        textureFileName: String,
        unlockCondition: String? = null
    ) : this(id.name, displayName, textureFileName, unlockCondition)
    
    val skinId: AsteroidSkinId get() = AsteroidSkinId.valueOf(id)
}
