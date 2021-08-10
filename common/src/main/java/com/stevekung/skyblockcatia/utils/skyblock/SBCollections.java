package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record SBCollections(ItemStack itemStack, com.stevekung.skyblockcatia.utils.skyblock.SBCollections.Type type, int value, int level)
{
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