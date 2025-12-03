package com.example.asteroidsredux.progression

object UpgradeCatalog {
    val upgrades = listOf(
        UpgradeDefinition(
            id = UpgradeId.DAMAGE, // TODO HOW DOES IT WORK ?
            displayName = "Damage",
            maxLevel = 5,
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
            maxLevel = 5,
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
            maxLevel = 3,
            descriptionPerLevel = listOf(
                "Bullet speed +25%",
                "Bullet speed +50%",
                "Bullet speed +75%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MULTI_SHOT, // TODO SUPER_UPGRADE
            displayName = "Multi Shot",
            maxLevel = 3,
            descriptionPerLevel = listOf(
                "+1 extra bullet",
                "+2 extra bullets",
                "+3 extra bullets"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.PIERCE, // TODO SUPER_UPGRADE
            displayName = "Pierce",
            maxLevel = 2,
            descriptionPerLevel = listOf(
                "Bullets pierce 1 enemy",
                "Bullets pierce 2 enemies"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MOVE_SPEED,
            displayName = "Move Speed",
            maxLevel = 4,
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
            maxLevel = 3,
            descriptionPerLevel = listOf(
                "Max HP +1",
                "Max HP +1",
                "Max HP +1"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.SHIELD,
            displayName = "Shield",
            maxLevel = 2,
            descriptionPerLevel = listOf(
                "Active Shield (12s recharge)",
                "Shield recharge reduced to 7s"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.MAGNET,
            displayName = "Magnet",
            maxLevel = 3,
            descriptionPerLevel = listOf(
                "XP Gain +5%",
                "XP Gain +10%",
                "XP Gain +15%"
            )
        ),
        UpgradeDefinition(
            id = UpgradeId.EXPLOSION_RADIUS, // TODO SUPER_UPGRADE
            displayName = "Explosive Rounds",
            maxLevel = 3,
            descriptionPerLevel = listOf(
                "Small explosion on kill",
                "Explosion radius +25%",
                "Explosion radius +50%"
            )
        )
    )
}
