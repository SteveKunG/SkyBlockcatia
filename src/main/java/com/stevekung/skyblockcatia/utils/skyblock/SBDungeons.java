package com.stevekung.skyblockcatia.utils.skyblock;

import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

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
        SPIRIT_LEAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.BLUE + "Spirit Leap", new ItemStack(Items.ender_pearl), true),
        DUNGEON_DECOY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Decoy", new ItemStack(Items.spawn_egg), false),
        INFLATABLE_JERRY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.WHITE + "Inflatable Jerry", new ItemStack(Items.spawn_egg, 0, EntityList.getIDFromString("Villager")), false),
        DUNGEON_TRAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Dungeon Trap", new ItemStack(Blocks.heavy_weighted_pressure_plate), false);

        private final String displayName;
        private final ItemStack baseItem;
        private final boolean enchanted;

        Drops(String displayName, ItemStack baseItem, boolean enchanted)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
            this.enchanted = enchanted;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getBaseItem()
        {
            return this.baseItem;
        }

        public boolean isEnchanted()
        {
            return this.enchanted;
        }
    }
}