package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SBCollections
{
    private final ItemStack itemStack;
    private final Type type;
    private final int value;
    private final int level;

    public SBCollections(ItemStack itemStack, Type type, int value, int level)
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
        return NumberUtils.NUMBER_FORMAT.format(this.value);
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

        Type(String name)
        {
            this.name = name;
        }

        public Component getName()
        {
            return TextComponentUtils.formatted(this.name, ChatFormatting.YELLOW, ChatFormatting.BOLD, ChatFormatting.UNDERLINE);
        }
    }
}