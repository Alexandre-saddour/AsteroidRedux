package com.example.asteroidsredux.skins

enum class BackgroundSkinId {
    STARS,
    VOID
}

data class BackgroundSkin(
    override val id: String,
    override val displayName: String,
    override val atlasRegionName: String,
    override val unlockCondition: String? = null
) : Skin
