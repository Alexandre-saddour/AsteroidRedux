package com.example.asteroidsredux.progression

object UpgradeCatalog {
    val upgrades = listOf(
        UpgradeDefinition(
            id = UpgradeId.DAMAGE, // TODO HOW DOES IT WORK ?
            displayName = "Damage",
            descriptionPerLevel = listOf(
                "Bullets deal +20% damage",
                "Bullets deal +40% damage",
                "Bullets deal +60% damage",
                "Bullets deal +80% damage",
                "Bullets deal +100% damage"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.FIRE_RATE,
            displayName = "Fire Rate",
            descriptionPerLevel = listOf(
                "Fire rate +12%",
                "Fire rate +24%",
                "Fire rate +36%",
                "Fire rate +48%",
                "Fire rate +60%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.BULLET_SPEED,
            displayName = "Bullet Speed",
            descriptionPerLevel = listOf(
                "Bullet speed +25%",
                "Bullet speed +50%",
                "Bullet speed +75%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MULTI_SHOT, // TODO SUPER_UPGRADE
            displayName = "Multi Shot",
            descriptionPerLevel = listOf(
                "+1 extra bullet",
                "+2 extra bullets",
                "+3 extra bullets"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.PIERCE, // TODO SUPER_UPGRADE
            displayName = "Pierce",
            descriptionPerLevel = listOf(
                "Bullets pierce 1 enemy",
                "Bullets pierce 2 enemies"
            )
        ),
//        UpgradeDefinition(
//            id = UpgradeId.BULLET_SIZE, // TODO SUPER_UPGRADE
//            displayName = "Bigger bullets",
//            descriptionPerLevel = listOf(
//                "Bigger bullets",
//            ),
//        ),
        UpgradeDefinition(
            id = UpgradeId.MOVE_SPEED,
            displayName = "Move Speed",
            descriptionPerLevel = listOf(
                "Ship speed +15%",
                "Ship speed +30%",
                "Ship speed +45%",
                "Ship speed +60%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MAX_HP,
            displayName = "Max HP",
            descriptionPerLevel = listOf(
                "Max HP +1",
                "Max HP +1",
                "Max HP +1"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.SHIELD,
            displayName = "Shield",
            descriptionPerLevel = listOf(
                "Active Shield (12s recharge)",
                "Shield recharge reduced to 7s"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MAGNET,
            displayName = "Magnet",
            descriptionPerLevel = listOf(
                "XP Gain +5%",
                "XP Gain +10%",
                "XP Gain +15%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.EXPLOSION_RADIUS, // TODO SUPER_UPGRADE
            displayName = "Explosive Rounds",
            descriptionPerLevel = listOf(
                "Small explosion on kill",
                "Explosion radius +25%",
                "Explosion radius +50%"
            )
        )
    )
}
