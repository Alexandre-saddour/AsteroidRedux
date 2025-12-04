package com.example.asteroidsredux.skins

object AsteroidSkinCatalog {
    val skins: List<AsteroidSkin> = listOf(
        AsteroidSkin(
            id = AsteroidSkinId.CLASSIC,
            displayName = "Classic",
            textureFileName = "", // No texture for classic (vector graphics)
            unlockCondition = null
        ),
        AsteroidSkin(
            id = AsteroidSkinId.ROCK,
            displayName = "Rock",
            textureFileName = "images/asteroid_rock.png",
            unlockCondition = null // Unlocked by default for now
        ),
    )

    fun getSkin(id: AsteroidSkinId): AsteroidSkin {
        return skins.first { it.id == id.name }
    }
}
