package com.example.asteroidsredux.skins

class SkinManager {
    // Set of unlocked skin IDs
    private val unlockedSkins = mutableSetOf(ShipSkinId.DEFAULT)
    
    // Currently selected skin
    var selectedSkinId: ShipSkinId = ShipSkinId.DEFAULT
        private set
    
    // Listeners for skin changes
    private val skinChangeListeners = mutableListOf<(ShipSkinId) -> Unit>()
    
    fun isUnlocked(id: ShipSkinId): Boolean = id in unlockedSkins
    
    fun unlockSkin(id: ShipSkinId) {
        unlockedSkins.add(id)
    }
    
    fun selectSkin(id: ShipSkinId): Boolean {
        if (!isUnlocked(id)) return false
        
        selectedSkinId = id
        notifyListeners()
        return true
    }
    
    fun getSelectedSkin(): ShipSkin = ShipSkinCatalog.getSkin(selectedSkinId)
    
    fun getUnlockedSkins(): List<ShipSkin> {
        return ShipSkinCatalog.skins.filter { isUnlocked(it.id) }
    }
    
    fun getAllSkins(): List<ShipSkin> = ShipSkinCatalog.skins
    
    // Listener support for in-game switching
    fun addSkinChangeListener(listener: (ShipSkinId) -> Unit) {
        skinChangeListeners.add(listener)
    }
    
    fun removeSkinChangeListener(listener: (ShipSkinId) -> Unit) {
        skinChangeListeners.remove(listener)
    }
    
    private fun notifyListeners() {
        skinChangeListeners.forEach { it(selectedSkinId) }
    }
}
