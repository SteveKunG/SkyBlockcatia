package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.BufferedReader;

import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;

public class SBSkills
{
    public static SkillCap SKILL_CAP;

    public static void getSkillsCap() throws Exception
    {
        BufferedReader in = DataGetter.get("api/default_skills_cap.json");
        SKILL_CAP = TextComponentUtils.GSON.fromJson(in, SkillCap.class);
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

    public static class SkillCap
    {
        private final int alchemy;
        private final int carpentry;
        private final int combat;
        private final int enchanting;
        private final int farming;
        private final int fishing;
        private final int foraging;
        private final int mining;
        private final int runecrafting;
        private final int taming;

        public SkillCap(int alchemy, int carpentry, int combat, int enchanting, int farming, int fishing, int foraging, int mining, int runecrafting, int taming)
        {
            this.alchemy = alchemy;
            this.carpentry = carpentry;
            this.combat = combat;
            this.enchanting = enchanting;
            this.farming = farming;
            this.fishing = fishing;
            this.foraging = foraging;
            this.mining = mining;
            this.runecrafting = runecrafting;
            this.taming = taming;
        }

        public int getCapBySkill(SBSkills.Type type)
        {
            switch (type)
            {
            case ALCHEMY:
                return this.alchemy;
            case CARPENTRY:
                return this.carpentry;
            case COMBAT:
                return this.combat;
            case ENCHANTING:
                return this.enchanting;
            case FARMING:
                return this.farming;
            case FISHING:
                return this.fishing;
            case FORAGING:
                return this.foraging;
            case MINING:
                return this.mining;
            case RUNECRAFTING:
                return this.runecrafting;
            case TAMING:
                return this.taming;
            default:
                return 50;
            }
        }
    }
}