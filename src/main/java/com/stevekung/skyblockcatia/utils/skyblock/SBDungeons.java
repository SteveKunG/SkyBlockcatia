package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class SBDungeons
{
    public enum Type
    {
        HEALER("Healer"),
        MAGE("Mage"),
        BERSERK("Berserk"),
        ARCHER("Archer"),
        TANK("Tank"),
        THE_CATACOMBS("The Catacombs");

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

    public enum Drops
    {
        SPIRIT_LEAP(TextComponentUtils.formatted("Spirit Leap", TextFormatting.BLUE), Items.ENDER_PEARL, true),
        DUNGEON_DECOY(TextComponentUtils.formatted("Decoy", TextFormatting.GREEN), Items.POLAR_BEAR_SPAWN_EGG, false),
        INFLATABLE_JERRY(TextComponentUtils.formatted("Inflatable Jerry", TextFormatting.WHITE), Items.VILLAGER_SPAWN_EGG, false),
        DUNGEON_TRAP(TextComponentUtils.formatted("Dungeon Trap", TextFormatting.GREEN), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, false);

        private final ITextComponent displayName;
        private final IItemProvider baseItem;
        private final boolean enchanted;

        private Drops(ITextComponent displayName, IItemProvider baseItem, boolean enchanted)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
            this.enchanted = enchanted;
        }

        public ITextComponent getDisplayName()
        {
            return this.displayName;
        }

        public IItemProvider getBaseItem()
        {
            return this.baseItem;
        }

        public boolean isEnchanted()
        {
            return this.enchanted;
        }
    }
}