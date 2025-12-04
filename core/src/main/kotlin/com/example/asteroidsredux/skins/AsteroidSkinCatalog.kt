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
            id = AsteroidSkinId.ROCK,
            displayName = "Rock",
            textureFileName = "images/asteroid_rock.png",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): AsteroidSkin = skins.first { it.id == id }
    
    fun getSkin(id: AsteroidSkinId): AsteroidSkin = getSkin(id.name)
}
