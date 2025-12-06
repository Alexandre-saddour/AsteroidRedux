package com.example.asteroidsredux.skins

object ShipSkinCatalog : SkinRegistry<ShipSkin> {
    override val skins: List<ShipSkin> = listOf(
        ShipSkin(
            id = ShipSkinId.CLASSIC,
            displayName = "Classic",
            atlasRegionName = "",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_1,
            displayName = "Sprite",
            atlasRegionName = "ship/ship1",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_2,
            displayName = "Sprite",
            atlasRegionName = "ship/ship2",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_3,
            displayName = "Sprite",
            atlasRegionName = "ship/ship3",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_4,
            displayName = "Sprite",
            atlasRegionName = "ship/ship4",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_5,
            displayName = "Sprite",
            atlasRegionName = "ship/ship5",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_6,
            displayName = "Sprite",
            atlasRegionName = "ship/ship6",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
        ShipSkin(
            id = ShipSkinId.SHIP_7,
            displayName = "Sprite",
            atlasRegionName = "ship/ship7",
            thrustAnimationRegionPrefix = "fire/skin1/fire",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): ShipSkin = skins.first { it.id == id }

    fun getSkin(id: ShipSkinId): ShipSkin = getSkin(id.name)
}
