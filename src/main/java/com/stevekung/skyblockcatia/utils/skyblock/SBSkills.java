package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SBSkills
{
    private static final Gson GSON = new Gson();
    public static SBSkills SKILLS;

    private final Map<String, Integer> cap;
    private final Map<String, int[]> leveling;
    private final Bonus bonus;

    public SBSkills(Map<String, Integer> cap, Map<String, int[]> leveling, Bonus bonus)
    {
        this.cap = cap;
        this.leveling = leveling;
        this.bonus = bonus;
    }

    public Map<String, Integer> getCap()
    {
        return this.cap;
    }

    public Map<String, int[]> getLeveling()
    {
        return this.leveling;
    }

    public Bonus getBonus()
    {
        return this.bonus;
    }

    public static void getSkills() throws IOException
    {
        SKILLS = GSON.fromJson(DataUtils.getData("skills.json"), SBSkills.class);
    }

    public static class Bonus
    {
        private final Farming[] farming;
        private final Mining[] mining;
        private final Combat[] combat;
        private final Foraging[] foraging;
        private final Fishing[] fishing;
        private final Enchanting[] enchanting;
        private final Alchemy[] alchemy;
        private final Taming[] taming;

        public Bonus(Farming[] farming, Mining[] mining, Combat[] combat, Foraging[] foraging, Fishing[] fishing, Enchanting[] enchanting, Alchemy[] alchemy, Taming[] taming)
        {
            this.farming = farming;
            this.mining = mining;
            this.combat = combat;
            this.foraging = foraging;
            this.fishing = fishing;
            this.enchanting = enchanting;
            this.alchemy = alchemy;
            this.taming = taming;
        }

        public Farming[] getFarming()
        {
            return this.farming;
        }

        public Mining[] getMining()
        {
            return this.mining;
        }

        public Combat[] getCombat()
        {
            return this.combat;
        }

        public Foraging[] getForaging()
        {
            return this.foraging;
        }

        public Fishing[] getFishing()
        {
            return this.fishing;
        }

        public Enchanting[] getEnchanting()
        {
            return this.enchanting;
        }

        public Alchemy[] getAlchemy()
        {
            return this.alchemy;
        }

        public Taming[] getTaming()
        {
            return this.taming;
        }
    }

    public static class Farming implements IBonusTemplate
    {
        private final int level;
        private final double health;
        @SerializedName("farming_fortune")
        private final double farmingFortune;

        public Farming(int level, double health, double farmingFortune)
        {
            this.level = level;
            this.health = health;
            this.farmingFortune = farmingFortune;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getHealth()
        {
            return this.health;
        }

        @Override
        public double getFarmingFortune()
        {
            return this.farmingFortune;
        }
    }

    public static class Foraging implements IBonusTemplate
    {
        private final int level;
        private final double strength;
        @SerializedName("foraging_fortune")
        private final double foragingFortune;

        public Foraging(int level, double strength, double foragingFortune)
        {
            this.level = level;
            this.strength = strength;
            this.foragingFortune = foragingFortune;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getStrength()
        {
            return this.strength;
        }

        @Override
        public double getForagingFortune()
        {
            return this.foragingFortune;
        }
    }

    public static class Mining implements IBonusTemplate
    {
        private final int level;
        private final double defense;
        @SerializedName("mining_fortune")
        private final double miningFortune;

        public Mining(int level, double defense, double miningFortune)
        {
            this.level = level;
            this.defense = defense;
            this.miningFortune = miningFortune;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getDefense()
        {
            return this.defense;
        }

        @Override
        public double getMiningFortune()
        {
            return this.miningFortune;
        }
    }

    public static class Fishing implements IBonusTemplate
    {
        private final int level;
        private final double health;

        public Fishing(int level, double health)
        {
            this.level = level;
            this.health = health;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getHealth()
        {
            return this.health;
        }
    }

    public static class Combat implements IBonusTemplate
    {
        private final int level;
        @SerializedName("crit_chance")
        private final double critChance;

        public Combat(int level, double critChance)
        {
            this.level = level;
            this.critChance = critChance;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getCritChance()
        {
            return this.critChance;
        }
    }

    public static class Enchanting implements IBonusTemplate
    {
        private final int level;
        private final double intelligence;
        @SerializedName("ability_damage")
        private final double abilityDamage;

        public Enchanting(int level, double intelligence, double abilityDamage)
        {
            this.level = level;
            this.intelligence = intelligence;
            this.abilityDamage = abilityDamage;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getIntelligence()
        {
            return this.intelligence;
        }

        @Override
        public double getAbilityDamage()
        {
            return this.abilityDamage;
        }
    }

    public static class Alchemy implements IBonusTemplate
    {
        private final int level;
        private final double intelligence;

        public Alchemy(int level, double intelligence)
        {
            this.level = level;
            this.intelligence = intelligence;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getIntelligence()
        {
            return this.intelligence;
        }
    }

    public static class Taming implements IBonusTemplate
    {
        private final int level;
        @SerializedName("pet_luck")
        private final double petLuck;

        public Taming(int level, double petLuck)
        {
            this.level = level;
            this.petLuck = petLuck;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getPetLuck()
        {
            return this.petLuck;
        }
    }

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

        Type(String name)
        {
            this(name, Blocks.air);
        }

        Type(String name, Block block)
        {
            this(name, new ItemStack(block));
        }

        Type(String name, Item item)
        {
            this(name, new ItemStack(item));
        }

        Type(String name, ItemStack itemStack)
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
                if (type.name().equals(name.toUpperCase(Locale.ROOT)))
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