package com.example.asteroidsredux.skins

object AsteroidSkinCatalog : SkinRegistry<AsteroidSkin> {
    override val skins: List<AsteroidSkin> = listOf(
        AsteroidSkin(
            id = AsteroidSkinId.CLASSIC,
            displayName = "Classic",
            textureFileName = "",
            unlockCondition = null
        ),
        AsteroidSkin(
            id = AsteroidSkinId.ROCK1,
            displayName = "Rock",
            textureFileName = "images/rock1.png",
            unlockCondition = null
        ),
        AsteroidSkin(
            id = AsteroidSkinId.ROCK2,
            displayName = "Rock",
            textureFileName = "images/rock2.png",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): AsteroidSkin = skins.first { it.id == id }

    fun getSkin(id: AsteroidSkinId): AsteroidSkin = getSkin(id.name)
}
