package com.example.asteroidsredux.skins

object AsteroidSkinCatalog : SkinRegistry<AsteroidSkin> {
    override val skins: List<AsteroidSkin> = listOf(
        AsteroidSkin(
            id = AsteroidSkinId.CLASSIC,
            displayName = "Classic",
            atlasRegionName = "",
            unlockCondition = null
        ),
        AsteroidSkin(
            id = AsteroidSkinId.ROCK1,
            displayName = "Rock",
            atlasRegionName = "rock/rock1",
            unlockCondition = null
        ),
        AsteroidSkin(
            id = AsteroidSkinId.ROCK2,
            displayName = "Rock",
            atlasRegionName = "rock/rock2",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): AsteroidSkin = skins.first { it.id == id }

    fun getSkin(id: AsteroidSkinId): AsteroidSkin = getSkin(id.name)
}
