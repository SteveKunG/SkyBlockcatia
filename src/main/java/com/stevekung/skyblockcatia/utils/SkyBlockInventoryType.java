package com.stevekung.skyblockcatia.utils;

public enum SkyBlockInventoryType
{
    ARMOR("inv_armor", "Armor"),
    INVENTORY("inv_contents", "Main Inventory"),
    ENDER_CHEST("ender_chest_contents", "Ender Chest"),
    PERSONAL_VAULT("personal_vault_contents", "Personal Vault"),
    ACCESSORY_BAG("talisman_bag", "Accessory Bag"),
    POTION_BAG("potion_bag", "Potion Bag"),
    FISHING_BAG("fishing_bag", "Fishing Bag"),
    WARDROBE("wardrobe_contents", "Wardrobe"),
    QUIVER("quiver", "Quiver"),
    CANDY("candy_inventory_contents", "Candy Bag");

    private final String apiName;
    private final String name;

    private SkyBlockInventoryType(String apiName, String name)
    {
        this.apiName = apiName;
        this.name = name;
    }

    public String getApiName()
    {
        return this.apiName;
    }

    public String getName()
    {
        return this.name;
    }
}