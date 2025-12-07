package com.example.asteroidsredux.skins

object BackgroundSkinCatalog : SkinRegistry<BackgroundSkin> {
    override val skins: List<BackgroundSkin> = listOf(
        BackgroundSkin(
            id = BackgroundSkinId.STARS.name,
            displayName = "Stars",
            atlasRegionName = "bg_stars",
            unlockCondition = null
        ),
        BackgroundSkin(
            id = BackgroundSkinId.VOID.name,
            displayName = "Void",
            atlasRegionName = "",
            unlockCondition = null
        )
    )

    override fun getSkin(id: String): BackgroundSkin {
        return skins.find { it.id == id } ?: skins.first()
    }
    
    fun getSkin(id: BackgroundSkinId): BackgroundSkin = getSkin(id.name)
}
