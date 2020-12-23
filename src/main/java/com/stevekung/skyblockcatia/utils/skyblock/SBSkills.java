package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

public class SBSkills
{
    public static ItemStack getSkillItemStack(String exp, SBSkills.Type skill)
    {
        ItemStack itemStack = skill.getItemStack();
        itemStack.setDisplayName(TextComponentUtils.component(exp + " " + skill.getName() + " XP").setStyle(Style.EMPTY.setColor(Color.fromHex(ColorUtils.toHex(255, 255, 85)))));
        return itemStack;
    }

    public enum Type
    {
        FARMING("Farming", Items.DIAMOND_HOE),
        FORAGING("Foraging", Items.DIAMOND_AXE),
        MINING("Mining", Items.DIAMOND_PICKAXE),
        FISHING("Fishing", Items.FISHING_ROD),
        COMBAT("Combat", Items.DIAMOND_SWORD),
        ENCHANTING("Enchanting", Blocks.ENCHANTING_TABLE),
        ALCHEMY("Alchemy", Items.BREWING_STAND),
        RUNECRAFTING("Runecrafting"),
        CARPENTRY("Carpentry"),
        TAMING("Taming");

        private final String name;
        private final ItemStack itemStack;

        private Type(String name)
        {
            this(name, ItemStack.EMPTY);
        }

        private Type(String name, IItemProvider item)
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