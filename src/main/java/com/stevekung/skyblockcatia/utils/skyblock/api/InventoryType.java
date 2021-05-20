package com.stevekung.skyblockcatia.utils.skyblock.api;

public enum InventoryType
{
    ARMOR("Armor"),
    INVENTORY("Main Inventory"),
    ENDER_CHEST("Ender Chest"),
    PERSONAL_VAULT("Personal Vault"),
    ACCESSORY_BAG("Accessory Bag"),
    POTION_BAG("Potion Bag"),
    FISHING_BAG("Fishing Bag"),
    WARDROBE("Wardrobe"),
    QUIVER("Quiver"),
    CANDY("Candy Bag");

    private final String name;

    InventoryType(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}