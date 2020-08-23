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

    public enum Type
    {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FORAGING("Foraging"),
        FISHING("Fishing"),
        UNKNOWN("Unknown");

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