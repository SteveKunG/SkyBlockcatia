package com.stevekung.skyblockcatia.utils.skyblock;

public enum SBLocation
{
    NONE("NONE", Type.OTHERS),
    YOUR_ISLAND("Your Island", Type.OTHERS),

    // Public Island
    VILLAGE("Village", Type.HUB),
    FARM("Farm", Type.HUB),
    COAL_MINE("Coal Mine", Type.HUB),
    FOREST("Forest", Type.HUB),
    RUINS("Ruins", Type.HUB),
    MOUNTAIN("Mountain", Type.HUB),
    HIGH_LEVEL("High Level", Type.HUB),
    GRAVEYARD("Graveyard", Type.HUB),
    WILDERNESS("Wilderness", Type.HUB),
    FISHERMAN_HUT("Fisherman's Hut", Type.HUB),
    COLOSSEUM("Colosseum", Type.HUB),
    BAZAAR_ALLEY("Bazaar Alley", Type.HUB),
    CATACOMBS_ENTRANCE("Catacombs Entrance", Type.HUB),

    // Public Island building
    FLOWER_HOUSE("Flower House", Type.BUILDING),
    LIBRARY("Library", Type.BUILDING),
    BANK("Bank", Type.BUILDING),
    AUCTION_HOUSE("Auction House", Type.BUILDING),
    TAVERN("Tavern", Type.BUILDING),
    FASHION_SHOP("Fashion Shop", Type.BUILDING),
    WIZARD_TOWER("Wizard Tower", Type.BUILDING),

    BLAZING_FORTRESS("Blazing Fortress", Type.OTHERS),

    SPIDER_DEN("Spider's Den", Type.OTHERS),

    GOLD_MINE("Gold Mine", Type.OTHERS),
    GUNPOWDER_MINES("Gunpowder Mines", Type.OTHERS),

    THE_END("The End", Type.THE_END),
    DRAGON_NEST("Dragon's Nest", Type.THE_END),
    ;

    public static final SBLocation[] VALUES = SBLocation.values();
    private final String location;
    private final Type type;

    private SBLocation(String location, Type type)
    {
        this.location = location;
        this.type = type;
    }

    public String getLocation()
    {
        return this.location;
    }

    public boolean isHub()
    {
        return this.type == Type.HUB || this.type == Type.BUILDING;
    }

    public boolean isTheEnd()
    {
        return this.type == Type.THE_END;
    }

    public boolean isShopOutsideHub()
    {
        return this == THE_END || this == GOLD_MINE || this == GUNPOWDER_MINES;
    }

    public enum Type
    {
        HUB,
        BUILDING,
        THE_END,
        OTHERS;
    }
}