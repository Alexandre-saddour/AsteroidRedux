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
            id = ShipSkinId.DEFAULT,
            displayName = "Sprite",
            textureFileName = "images/ship1.png",
            unlockCondition = null
        ),
    )

    override fun getSkin(id: String): ShipSkin = skins.first { it.id == id }
    
    fun getSkin(id: ShipSkinId): ShipSkin = getSkin(id.name)
}
