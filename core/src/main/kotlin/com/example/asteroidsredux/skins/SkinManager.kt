package com.example.asteroidsredux.skins

/**
 * Central manager for all skin categories.
 * Handles unlocking, selecting, and notifying listeners of skin changes.
 */
class SkinManager {
    // Unlocked skins per category: Map<Category, Set<SkinId>>
    private val unlockedSkins = mutableMapOf<SkinCategory, MutableSet<String>>().apply {
        put(SkinCategory.SHIP, mutableSetOf(ShipSkinId.CLASSIC.name, ShipSkinId.DEFAULT.name))
        put(SkinCategory.ASTEROID, mutableSetOf(AsteroidSkinId.CLASSIC.name, AsteroidSkinId.ROCK.name))
    }
    
    // Selected skin per category: Map<Category, SkinId>
    private val selectedSkins = mutableMapOf<SkinCategory, String>().apply {
        put(SkinCategory.SHIP, ShipSkinId.CLASSIC.name)
        put(SkinCategory.ASTEROID, AsteroidSkinId.CLASSIC.name)
    }
    
    // Listeners per category
    private val listeners = mutableMapOf<SkinCategory, MutableList<(String) -> Unit>>()
    
    // --- Generic API ---
    
    fun isUnlocked(category: SkinCategory, skinId: String): Boolean {
        return unlockedSkins[category]?.contains(skinId) == true
    }
    
    fun unlockSkin(category: SkinCategory, skinId: String) {
        unlockedSkins.getOrPut(category) { mutableSetOf() }.add(skinId)
    }
    
    fun selectSkin(category: SkinCategory, skinId: String): Boolean {
        if (!isUnlocked(category, skinId)) return false
        selectedSkins[category] = skinId
        notifyListeners(category, skinId)
        return true
    }
    
    fun getSelectedSkinId(category: SkinCategory): String? = selectedSkins[category]
    
    fun getSkinsForCategory(category: SkinCategory): List<Skin> {
        return when (category) {
            SkinCategory.SHIP -> ShipSkinCatalog.skins
            SkinCategory.ASTEROID -> AsteroidSkinCatalog.skins
        }
    }
    
    fun addListener(category: SkinCategory, listener: (String) -> Unit) {
        listeners.getOrPut(category) { mutableListOf() }.add(listener)
    }
    
    fun removeListener(category: SkinCategory, listener: (String) -> Unit) {
        listeners[category]?.remove(listener)
    }
    
    private fun notifyListeners(category: SkinCategory, skinId: String) {
        listeners[category]?.forEach { it(skinId) }
    }
    
    // --- Ship-specific convenience methods ---
    
    var selectedShipSkinId: ShipSkinId
        get() = ShipSkinId.valueOf(selectedSkins[SkinCategory.SHIP] ?: ShipSkinId.DEFAULT.name)
        private set(value) { selectedSkins[SkinCategory.SHIP] = value.name }
    
    fun selectShipSkin(id: ShipSkinId): Boolean = selectSkin(SkinCategory.SHIP, id.name)
    
    fun getSelectedShipSkin(): ShipSkin = ShipSkinCatalog.getSkin(selectedShipSkinId)
    
    fun addShipSkinChangeListener(listener: (ShipSkinId) -> Unit) {
        addListener(SkinCategory.SHIP) { skinId -> listener(ShipSkinId.valueOf(skinId)) }
    }

    // --- Asteroid-specific convenience methods ---

    var selectedAsteroidSkinId: AsteroidSkinId
        get() = AsteroidSkinId.valueOf(selectedSkins[SkinCategory.ASTEROID] ?: AsteroidSkinId.CLASSIC.name)
        private set(value) { selectedSkins[SkinCategory.ASTEROID] = value.name }

    fun selectAsteroidSkin(id: AsteroidSkinId): Boolean = selectSkin(SkinCategory.ASTEROID, id.name)

    fun getSelectedAsteroidSkin(): AsteroidSkin = AsteroidSkinCatalog.getSkin(selectedAsteroidSkinId)

    fun addAsteroidSkinChangeListener(listener: (AsteroidSkinId) -> Unit) {
        addListener(SkinCategory.ASTEROID) { skinId -> listener(AsteroidSkinId.valueOf(skinId)) }
    }
}
