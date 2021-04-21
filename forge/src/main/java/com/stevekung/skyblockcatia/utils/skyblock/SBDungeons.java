package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

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

        Type(String name)
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
        SPIRIT_LEAP(TextComponentUtils.formatted("Spirit Leap", ChatFormatting.BLUE), Items.ENDER_PEARL, true),
        DUNGEON_DECOY(TextComponentUtils.formatted("Decoy", ChatFormatting.GREEN), Items.POLAR_BEAR_SPAWN_EGG, false),
        INFLATABLE_JERRY(TextComponentUtils.formatted("Inflatable Jerry", ChatFormatting.WHITE), Items.VILLAGER_SPAWN_EGG, false),
        DUNGEON_TRAP(TextComponentUtils.formatted("Dungeon Trap", ChatFormatting.GREEN), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, false);

        private final Component displayName;
        private final ItemLike baseItem;
        private final boolean enchanted;

        Drops(Component displayName, ItemLike baseItem, boolean enchanted)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
            this.enchanted = enchanted;
        }

        public Component getDisplayName()
        {
            return this.displayName;
        }

        public ItemLike getBaseItem()
        {
            return this.baseItem;
        }

        public boolean isEnchanted()
        {
            return this.enchanted;
        }
    }
}