package com.example.asteroidsredux.progression

enum class UpgradeId {
    DAMAGE,
    FIRE_RATE,
    BULLET_SPEED,
    MULTI_SHOT,
    PIERCE,
    MOVE_SPEED,
    MAX_HP,
    SHIELD,
    MAGNET,
    EXPLOSION_RADIUS,
//    BULLET_SIZE,
}

data class UpgradeDefinition(
    val id: UpgradeId,
    val displayName: String,
    val descriptionPerLevel: List<String>,
) {
    val maxLevel = descriptionPerLevel.size
}
