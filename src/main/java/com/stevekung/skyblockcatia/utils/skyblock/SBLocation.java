package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum SBLocation
{
    NONE("None"),
    YOUR_ISLAND("Your Island"),

    // Public Island
    VILLAGE("Village", ImmutableList.of(Blocks.POPPY, Blocks.DANDELION, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CARROTS, Blocks.POTATOES, Blocks.WHEAT, Blocks.NETHER_WART)),
    FARM("Farm", ImmutableList.of(Blocks.WHEAT)),
    FARMHOUSE("Farmhouse", ImmutableList.of(Blocks.POTATOES, Blocks.WHEAT, Blocks.NETHER_WART)),
    COAL_MINE("Coal Mine", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.COAL_ORE)),
    FOREST("Forest", ImmutableList.of(Blocks.OAK_LOG, Blocks.OAK_LEAVES)),
    RUINS("Ruins"),
    MOUNTAIN("Mountain", ImmutableList.of(Blocks.CARROTS)),
    HIGH_LEVEL("High Level"),
    GRAVEYARD("Graveyard"),
    WILDERNESS("Wilderness"),
    FISHERMAN_HUT("Fisherman's Hut"),
    COLOSSEUM("Colosseum"),
    BAZAAR_ALLEY("Bazaar Alley"),
    CATACOMBS_ENTRANCE("Catacombs Entrance"),

    // Public Island building
    FLOWER_HOUSE("Flower House", ImmutableList.of(Blocks.POPPY, Blocks.DANDELION, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY)),
    LIBRARY("Library"),
    BANK("Bank"),
    AUCTION_HOUSE("Auction House"),
    TAVERN("Tavern"),
    FASHION_SHOP("Fashion Shop"),
    WIZARD_TOWER("Wizard Tower"),

    THE_BARN("The Barn", ImmutableList.of(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.PUMPKIN, Blocks.MELON)),
    MUSHROOM_DESERT("Mushroom Desert", ImmutableList.of(Blocks.COCOA, Blocks.SUGAR_CANE, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.CACTUS)),

    BIRCH_PARK("Birch Park", ImmutableList.of(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES)),
    SPRUCE_WOODS("Spruce Woods", ImmutableList.of(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES)),
    DARK_THICKET("Dark Thicket", ImmutableList.of(Blocks.DARK_OAK_LOG, Blocks.OAK_LEAVES)),
    SAVANNA_WOODLAND("Savanna Woodland", ImmutableList.of(Blocks.ACACIA_LOG, Blocks.OAK_LEAVES)),
    JUNGLE_ISLAND("Jungle Island", ImmutableList.of(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES)),

    BLAZING_FORTRESS("Blazing Fortress", ImmutableList.of(Blocks.NETHERRACK, Blocks.NETHER_QUARTZ_ORE, Blocks.GLOWSTONE, Blocks.NETHER_WART)),

    SPIDER_DEN("Spider's Den", ImmutableList.of(Blocks.GRAVEL)),

    GOLD_MINE("Gold Mine", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE)),
    DEEP_CAVERNS("Deep Caverns", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE)),
    GUNPOWDER_MINES("Gunpowder Mines", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE)),
    LAPIS_QUARRY("Lapis Quarry", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.LAPIS_ORE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE)),
    PIGMENS_DEN("Pigmen's Den", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.REDSTONE_ORE, Blocks.LAPIS_ORE, Blocks.EMERALD_ORE)),
    SLIMEHILL("Slimehill", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.EMERALD_ORE, Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE)),
    DIAMOND_RESERVE("Diamond Reserve", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE)),
    OBSIDIAN_SANCTUARY("Obsidian Sanctuary", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.OBSIDIAN)),

    THE_END("The End", ImmutableList.of(Blocks.END_STONE, Blocks.OBSIDIAN)),
    DRAGON_NEST("Dragon's Nest", ImmutableList.of(Blocks.END_STONE, Blocks.OBSIDIAN)),

    JERRY_WORKSHOP("Jerry's Workshop", ImmutableList.of(Blocks.ICE)),
    JERRY_POND("Jerry Pond", ImmutableList.of(Blocks.ICE)),

    DUNGEON_HUB("Dungeon Hub"),
    DUNGEON("Dungeon"),
    THE_CATACOMBS_E("The Catacombs (E)"),
    THE_CATACOMBS_F1("The Catacombs (F1)"),
    THE_CATACOMBS_F2("The Catacombs (F2)"),
    THE_CATACOMBS_F3("The Catacombs (F3)"),
    THE_CATACOMBS_F4("The Catacombs (F4)"),
    THE_CATACOMBS_F5("The Catacombs (F5)"),
    THE_CATACOMBS_F6("The Catacombs (F6)"),
    THE_CATACOMBS_F7("The Catacombs (F7)"),
    ;

    public static final SBLocation[] VALUES = SBLocation.values();
    private final String location;
    private final ImmutableList<Block> mineableList;

    private SBLocation(String location)
    {
        this(location, ImmutableList.of());
    }

    private SBLocation(String location, ImmutableList<Block> mineableList)
    {
        this.location = location;
        this.mineableList = mineableList;
    }

    public boolean isTheEnd()
    {
        return this == SBLocation.THE_END || this == SBLocation.DRAGON_NEST;
    }

    public boolean ignore()
    {
        return this == SBLocation.YOUR_ISLAND || this == SBLocation.THE_CATACOMBS_E || this == SBLocation.THE_CATACOMBS_F1 || this == SBLocation.THE_CATACOMBS_F2 || this == SBLocation.THE_CATACOMBS_F3 || this == SBLocation.THE_CATACOMBS_F4 || this == SBLocation.THE_CATACOMBS_F5 || this == SBLocation.THE_CATACOMBS_F6 || this == SBLocation.THE_CATACOMBS_F7;
    }

    public String getLocation()
    {
        return this.location;
    }

    public ImmutableList<Block> getMineableList()
    {
        return this.mineableList;
    }
}