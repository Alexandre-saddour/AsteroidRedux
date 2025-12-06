package com.example.asteroidsredux.skins

enum class AsteroidSkinId {
    CLASSIC,
    ROCK1,
    ROCK2,
    // Add more skins here
    // ICE,
    // LAVA,
}

data class AsteroidSkin(
    override val id: String,
    override val displayName: String,
    override val atlasRegionName: String, // For CLASSIC, this can be ignored or empty
    override val unlockCondition: String? = null
) : Skin {
    constructor(
        id: AsteroidSkinId,
        displayName: String,
        atlasRegionName: String,
        unlockCondition: String? = null
    ) : this(id.name, displayName, atlasRegionName, unlockCondition)

    val skinId: AsteroidSkinId get() = AsteroidSkinId.valueOf(id)
}
