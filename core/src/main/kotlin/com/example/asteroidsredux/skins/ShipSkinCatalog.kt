package com.example.asteroidsredux.skins

object ShipSkinCatalog {
    val skins: List<ShipSkin> = listOf(
        ShipSkin(
            id = ShipSkinId.DEFAULT,
            displayName = "Default",
            textureFileName = "images/ship1.png",
            unlockCondition = null // Unlocked by default
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
