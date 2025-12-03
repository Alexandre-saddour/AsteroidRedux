package com.example.asteroidsredux.progression

data class PlayerStats(
    var level: Int = 1,
    var currentXp: Int = 0,
    var xpToNextLevel: Int = 40,

    var maxHp: Int = 3,
    var currentHp: Int = 3,

    var baseDamage: Float = 1f,
    var fireCooldown: Float = 0.35f,
    var bulletSpeed: Float = 350f,
    var bulletLifetimeMultiplier: Float = 1f, // Range multiplier
    var bulletSizeMultiplier: Float = 1f,
    var shipAcceleration: Float = 300f,
    var shipMaxSpeed: Float = 450f,

    // Extra stats for upgrades
    var multiShotLevel: Int = 0,
    var pierceLevel: Int = 0,
    var shieldLevel: Int = 0, // todo: no used
    var shieldRechargeTime: Float = 0f,
    var magnetLevel: Int = 0,
    var explosionRadiusLevel: Int = 0, // todo: not used

    // upgrade levels:
    val upgradeLevels: MutableMap<UpgradeId, Int> = mutableMapOf()
)
