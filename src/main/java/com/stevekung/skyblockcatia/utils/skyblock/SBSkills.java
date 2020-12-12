package com.stevekung.skyblockcatia.utils.skyblock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SBSkills
{
    public enum Type
    {
        FARMING("Farming", Items.diamond_hoe),
        FORAGING("Foraging", Items.diamond_axe),
        MINING("Mining", Items.diamond_pickaxe),
        FISHING("Fishing", Items.fishing_rod),
        COMBAT("Combat", Items.diamond_sword),
        ENCHANTING("Enchanting", Blocks.enchanting_table),
        ALCHEMY("Alchemy", Items.brewing_stand),
        RUNECRAFTING("Runecrafting"),
        CARPENTRY("Carpentry"),
        TAMING("Taming");

        private final String name;
        private final ItemStack itemStack;

        private Type(String name)
        {
            this(name, Blocks.air);
        }

        private Type(String name, Block block)
        {
            this(name, new ItemStack(block));
        }

        private Type(String name, Item item)
        {
            this(name, new ItemStack(item));
        }

        private Type(String name, ItemStack itemStack)
        {
            this.name = name;
            this.itemStack = itemStack;
        }

        public String getName()
        {
            return this.name;
        }

        public ItemStack getItemStack()
        {
            return this.itemStack;
        }

        public boolean isCosmetic()
        {
            return this == RUNECRAFTING || this == CARPENTRY;
        }

        public static SBSkills.Type byName(String name)
        {
            for (SBSkills.Type type : SBSkills.Type.values())
            {
                if (type.name.equals(name))
                {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown skill type '" + name + "'");
        }
    }

    public static class Info
    {
        private final String name;
        private final double currentXp;
        private final int xpRequired;
        private final int currentLvl;
        private final double skillProgress;
        private final boolean reachLimit;

        public Info(String name, double currentXp, int xpRequired, int currentLvl, double skillProgress, boolean reachLimit)
        {
            this.name = name;
            this.currentXp = currentXp;
            this.xpRequired = xpRequired;
            this.currentLvl = currentLvl;
            this.skillProgress = skillProgress;
            this.reachLimit = reachLimit;
        }

        public String getName()
        {
            return this.name;
        }

        public double getCurrentXp()
        {
            return this.currentXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public int getCurrentLvl()
        {
            return this.currentLvl;
        }

        public double getSkillProgress()
        {
            return this.skillProgress;
        }

        public boolean isReachLimit()
        {
            return this.reachLimit;
        }
    }
}