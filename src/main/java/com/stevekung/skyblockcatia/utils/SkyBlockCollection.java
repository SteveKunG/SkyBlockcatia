package com.stevekung.skyblockcatia.utils;

import net.minecraft.item.ItemStack;

public class SkyBlockCollection
{
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###");
    private final ItemStack itemStack;
    private final Type type;
    private final int value;
    private final int level;

    public SkyBlockCollection(ItemStack itemStack, Type type, int value, int level)
    {
        this.itemStack = itemStack;
        this.type = type;
        this.value = value;
        this.level = level;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public Type getCollectionType()
    {
        return this.type;
    }

    public int getValue()
    {
        return this.value;
    }

    public int getLevel()
    {
        return this.level;
    }

    public String getCollectionAmount()
    {
        return FORMAT.format(this.value);
    }

    public enum ItemId
    {
        SEEDS("wheat_seeds"),
        RAW_CHICKEN("chicken"),
        CARROT_ITEM("carrot"),
        POTATO_ITEM("potato"),
        SULPHUR("gunpowder"),
        MUSHROOM_COLLECTION("red_mushroom"),
        SUGAR_CANE("reeds"),
        PORK("porkchop"),
        NETHER_STALK("nether_wart"),
        RAW_FISH("fish"),
        INK_SACK("dye"),
        WATER_LILY("waterlily"),
        ENDER_STONE("end_stone"),
        LOG_2("log2");

        public static final ItemId[] VALUES = ItemId.values();
        private final String minecraftId;

        private ItemId(String minecraftId)
        {
            this.minecraftId = minecraftId;
        }

        public String getMinecraftId()
        {
            return this.minecraftId;
        }
    }

    public enum Type
    {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FORAGING("Foraging"),
        FISHING("Fishing");

        private final String name;

        private Type(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}