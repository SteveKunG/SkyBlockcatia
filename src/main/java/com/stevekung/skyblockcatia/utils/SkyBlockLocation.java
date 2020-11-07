package com.stevekung.skyblockcatia.utils;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum SkyBlockLocation
{
    NONE("None"),
    YOUR_ISLAND("Your Island"),

    // Public Island
    VILLAGE("Village", ImmutableList.of(BlockMetadata.create(Blocks.red_flower), BlockMetadata.create(Blocks.yellow_flower), BlockMetadata.create(Blocks.carrots), BlockMetadata.create(Blocks.potatoes), BlockMetadata.create(Blocks.wheat), BlockMetadata.create(Blocks.nether_wart))),
    FARMHOUSE("Farmhouse", ImmutableList.of(BlockMetadata.create(Blocks.potatoes), BlockMetadata.create(Blocks.wheat), BlockMetadata.create(Blocks.nether_wart))),
    FARM("Farm", ImmutableList.of(BlockMetadata.create(Blocks.wheat))),
    COAL_MINE("Coal Mine", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.coal_ore))),
    FOREST("Forest", ImmutableList.of(BlockMetadata.create(Blocks.log), BlockMetadata.create(Blocks.leaves))),
    RUINS("Ruins"),
    MOUNTAIN("Mountain", ImmutableList.of(BlockMetadata.create(Blocks.carrots))),
    HIGH_LEVEL("High Level"),
    GRAVEYARD("Graveyard"),
    WILDERNESS("Wilderness"),
    FISHERMAN_HUT("Fisherman's Hut"),
    COLOSSEUM("Colosseum"),
    BAZAAR_ALLEY("Bazaar Alley"),
    COMMUNITY_CENTER("Community Center"),
    CATACOMBS_ENTRANCE("Catacombs Entrance"),

    // Public Island building
    FLOWER_HOUSE("Flower House", ImmutableList.of(BlockMetadata.create(Blocks.red_flower), BlockMetadata.create(Blocks.yellow_flower))),
    LIBRARY("Library"),
    BANK("Bank"),
    AUCTION_HOUSE("Auction House"),
    TAVERN("Tavern"),
    FASHION_SHOP("Fashion Shop"),
    WIZARD_TOWER("Wizard Tower"),

    THE_BARN("The Barn", ImmutableList.of(BlockMetadata.create(Blocks.wheat), BlockMetadata.create(Blocks.carrots), BlockMetadata.create(Blocks.potatoes), BlockMetadata.create(Blocks.pumpkin), BlockMetadata.create(Blocks.melon_block))),
    MUSHROOM_DESERT("Mushroom Desert", ImmutableList.of(BlockMetadata.create(Blocks.cocoa), BlockMetadata.create(Blocks.reeds), BlockMetadata.create(Blocks.red_mushroom), BlockMetadata.create(Blocks.brown_mushroom), BlockMetadata.create(Blocks.red_mushroom_block), BlockMetadata.create(Blocks.brown_mushroom_block), BlockMetadata.create(Blocks.cactus))),

    BIRCH_PARK("Birch Park", ImmutableList.of(BlockMetadata.create(Blocks.log), BlockMetadata.create(Blocks.leaves))),
    SPRUCE_WOODS("Spruce Woods", ImmutableList.of(BlockMetadata.create(Blocks.log), BlockMetadata.create(Blocks.leaves))),
    DARK_THICKET("Dark Thicket", ImmutableList.of(BlockMetadata.create(Blocks.log2), BlockMetadata.create(Blocks.leaves))),
    SAVANNA_WOODLAND("Savanna Woodland", ImmutableList.of(BlockMetadata.create(Blocks.log2), BlockMetadata.create(Blocks.leaves))),
    JUNGLE_ISLAND("Jungle Island", ImmutableList.of(BlockMetadata.create(Blocks.log), BlockMetadata.create(Blocks.leaves))),

    BLAZING_FORTRESS("Blazing Fortress", ImmutableList.of(BlockMetadata.create(Blocks.netherrack), BlockMetadata.create(Blocks.quartz_ore), BlockMetadata.create(Blocks.glowstone), BlockMetadata.create(Blocks.nether_wart))),

    SPIDER_DEN("Spider's Den", ImmutableList.of(BlockMetadata.create(Blocks.gravel))),

    GOLD_MINE("Gold Mine", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.coal_ore), BlockMetadata.create(Blocks.iron_ore), BlockMetadata.create(Blocks.gold_ore))),
    DEEP_CAVERNS("Deep Caverns", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone))),
    GUNPOWDER_MINES("Gunpowder Mines", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.coal_ore), BlockMetadata.create(Blocks.iron_ore), BlockMetadata.create(Blocks.gold_ore), BlockMetadata.create(Blocks.lapis_ore))),
    LAPIS_QUARRY("Lapis Quarry", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.lapis_ore), BlockMetadata.create(Blocks.coal_ore), BlockMetadata.create(Blocks.iron_ore), BlockMetadata.create(Blocks.gold_ore))),
    PIGMENS_DEN("Pigmen's Den", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.redstone_ore), BlockMetadata.create(Blocks.lit_redstone_ore), BlockMetadata.create(Blocks.lapis_ore), BlockMetadata.create(Blocks.emerald_ore))),
    SLIMEHILL("Slimehill", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.emerald_ore), BlockMetadata.create(Blocks.redstone_ore), BlockMetadata.create(Blocks.lit_redstone_ore), BlockMetadata.create(Blocks.diamond_ore))),
    DIAMOND_RESERVE("Diamond Reserve", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.diamond_ore), BlockMetadata.create(Blocks.emerald_ore))),
    OBSIDIAN_SANCTUARY("Obsidian Sanctuary", ImmutableList.of(BlockMetadata.create(Blocks.stone, 0), BlockMetadata.create(Blocks.cobblestone), BlockMetadata.create(Blocks.diamond_ore), BlockMetadata.create(Blocks.diamond_block), BlockMetadata.create(Blocks.obsidian))),

    THE_END("The End", ImmutableList.of(BlockMetadata.create(Blocks.end_stone), BlockMetadata.create(Blocks.obsidian))),
    DRAGON_NEST("Dragon's Nest", ImmutableList.of(BlockMetadata.create(Blocks.end_stone), BlockMetadata.create(Blocks.obsidian))),

    JERRY_WORKSHOP("Jerry's Workshop", ImmutableList.of(BlockMetadata.create(Blocks.ice))),
    JERRY_POND("Jerry Pond", ImmutableList.of(BlockMetadata.create(Blocks.ice))),

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
    THE_CATACOMBS_F8("The Catacombs (F8)"),
    THE_CATACOMBS_F9("The Catacombs (F9)"),
    THE_CATACOMBS_F10("The Catacombs (F10)"),
    ;

    private final String location;
    private final ImmutableList<BlockMetadata> mineableList;

    private SkyBlockLocation(String location)
    {
        this(location, ImmutableList.of());
    }

    private SkyBlockLocation(String location, ImmutableList<BlockMetadata> mineableList)
    {
        this.location = location;
        this.mineableList = mineableList;
    }

    public String getLocation()
    {
        return this.location;
    }

    public ImmutableList<BlockMetadata> getMineableList()
    {
        return this.mineableList;
    }

    public boolean isTheEnd()
    {
        return this == SkyBlockLocation.THE_END || this == SkyBlockLocation.DRAGON_NEST;
    }

    public boolean ignore()
    {
        return this == SkyBlockLocation.YOUR_ISLAND || this == SkyBlockLocation.THE_CATACOMBS_E || this == SkyBlockLocation.THE_CATACOMBS_F1 || this == SkyBlockLocation.THE_CATACOMBS_F2 || this == SkyBlockLocation.THE_CATACOMBS_F3 || this == SkyBlockLocation.THE_CATACOMBS_F4;
    }

    public static class BlockMetadata
    {
        private final Block block;
        private final int meta;

        private BlockMetadata(Block block)
        {
            this(block, 0);
        }

        private BlockMetadata(Block block, int meta)
        {
            this.block = block;
            this.meta = meta;
        }

        public static BlockMetadata create(Block block)
        {
            return BlockMetadata.create(block, -1);
        }

        public static BlockMetadata create(Block block, int meta)
        {
            return new BlockMetadata(block, meta);
        }

        public Block getBlock()
        {
            return this.block;
        }

        public int getMeta()
        {
            return this.meta;
        }
    }
}