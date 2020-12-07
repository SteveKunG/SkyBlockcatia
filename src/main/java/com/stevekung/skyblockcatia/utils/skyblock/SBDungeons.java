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
        SPIRIT_LEAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.BLUE + "Spirit Leap", new ItemStack(Items.ender_pearl)),
        DUNGEON_DECOY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Decoy", new ItemStack(Items.spawn_egg)),
        INFLATABLE_JERRY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.WHITE + "Inflatable Jerry", new ItemStack(Items.spawn_egg, 0, EntityList.getIDFromString("Villager"))),
        DUNGEON_TRAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Dungeon Trap", new ItemStack(Blocks.heavy_weighted_pressure_plate));

        private final String displayName;
        private final ItemStack baseItem;

        private Drops(String displayName, ItemStack baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getBaseItem()
        {
            return this.baseItem;
        }
    }
}