package com.example.asteroidsredux.skins

object ShipSkinCatalog : SkinRegistry<ShipSkin> {
    override val skins: List<ShipSkin> = listOf(
        ShipSkin(
            id = ShipSkinId.CLASSIC,
            displayName = "Classic",
            textureFileName = "",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_1,
            displayName = "Sprite",
            textureFileName = "images/ship1.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_2,
            displayName = "Sprite",
            textureFileName = "images/ship2.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_3,
            displayName = "Sprite",
            textureFileName = "images/ship3.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_4,
            displayName = "Sprite",
            textureFileName = "images/ship4.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_5,
            displayName = "Sprite",
            textureFileName = "images/ship5.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_6,
            displayName = "Sprite",
            textureFileName = "images/ship6.png",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_7,
            displayName = "Sprite",
            textureFileName = "images/ship7.png",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): ShipSkin = skins.first { it.id == id }

    fun getSkin(id: ShipSkinId): ShipSkin = getSkin(id.name)
}
