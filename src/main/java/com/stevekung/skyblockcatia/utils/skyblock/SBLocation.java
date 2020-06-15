package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public enum SBLocation
{
    NONE("None", ImmutableList.of()),
    YOUR_ISLAND("Your Island", ImmutableList.of()),

    // Public Island
    VILLAGE("Village", ImmutableList.of(Blocks.POPPY, Blocks.DANDELION, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY)),
    FARM("Farm", ImmutableList.of(Blocks.WHEAT)),
    COAL_MINE("Coal Mine", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.COAL_ORE)),
    FOREST("Forest", ImmutableList.of(Blocks.OAK_LOG, Blocks.OAK_LEAVES)),
    RUINS("Ruins", ImmutableList.of()),
    MOUNTAIN("Mountain", ImmutableList.of(Blocks.CARROTS)),
    HIGH_LEVEL("High Level", ImmutableList.of()),
    GRAVEYARD("Graveyard", ImmutableList.of()),
    WILDERNESS("Wilderness", ImmutableList.of()),
    FISHERMAN_HUT("Fisherman's Hut", ImmutableList.of()),
    COLOSSEUM("Colosseum", ImmutableList.of()),
    BAZAAR_ALLEY("Bazaar Alley", ImmutableList.of()),
    CATACOMBS_ENTRANCE("Catacombs Entrance", ImmutableList.of()),

    // Public Island building
    FLOWER_HOUSE("Flower House", ImmutableList.of(Blocks.POPPY, Blocks.DANDELION, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP)),
    LIBRARY("Library", ImmutableList.of()),
    BANK("Bank", ImmutableList.of()),
    AUCTION_HOUSE("Auction House", ImmutableList.of()),
    TAVERN("Tavern", ImmutableList.of()),
    FASHION_SHOP("Fashion Shop", ImmutableList.of()),
    WIZARD_TOWER("Wizard Tower", ImmutableList.of()),

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
    GUNPOWDER_MINES("Gunpowder Mines", ImmutableList.of(Blocks.STONE, Blocks.COBBLESTONE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE, Blocks.EMERALD_ORE, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.OBSIDIAN)),

    THE_END("The End", ImmutableList.of(Blocks.END_STONE, Blocks.OBSIDIAN)),
    DRAGON_NEST("Dragon's Nest", ImmutableList.of(Blocks.END_STONE, Blocks.OBSIDIAN)),
    ;

    public static final SBLocation[] VALUES = SBLocation.values();
    private final String location;
    private final ImmutableList<Block> mineableList;

    private SBLocation(String location, ImmutableList<Block> mineableList)
    {
        this.location = location;
        this.mineableList = mineableList;
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