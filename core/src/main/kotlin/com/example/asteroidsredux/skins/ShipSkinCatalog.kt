package com.example.asteroidsredux.skins

object ShipSkinCatalog {
    val skins: List<ShipSkin> = listOf(
        ShipSkin(
            id = ShipSkinId.CLASSIC,
            displayName = "Classic",
            textureFileName = "", // No texture for classic (vector graphics)
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.DEFAULT,
            displayName = "Sprite",
            textureFileName = "images/ship1.png",
            unlockCondition = null
        ),
        // Add more skins here:
        // ShipSkin(
        //     id = ShipSkinId.PHANTOM,
        //     displayName = "Phantom",
        //     textureFileName = "images/ship_phantom.png",
        //     unlockCondition = "Reach Level 10"
        // ),
    )

    fun getSkin(id: ShipSkinId): ShipSkin {
        return skins.first { it.id == id.name }
    }
}
