package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Locale;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public record SBSkills(Map<String, Integer> cap, Map<String, int[]> leveling, com.stevekung.skyblockcatia.utils.skyblock.SBSkills.Bonus bonus)
{
    public static SBSkills SKILLS;

    public static void getSkills()
    {
        SKILLS = TextComponentUtils.GSON.fromJson(DataUtils.getData("skills.json"), SBSkills.class);
    }

    public record Bonus(Farming[] farming, Mining[] mining, Combat[] combat, Foraging[] foraging, Fishing[] fishing, Enchanting[] enchanting, Alchemy[] alchemy, Taming[] taming) {}

    public record Farming(int level, double health, @SerializedName("farming_fortune") double farmingFortune) implements IBonusTemplate
    {
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

    public record Foraging(int level, double strength, @SerializedName("foraging_fortune") double foragingFortune) implements IBonusTemplate
    {
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

    public record Mining(int level, double defense, @SerializedName("mining_fortune") double miningFortune) implements IBonusTemplate
    {
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

    public record Fishing(int level, double health) implements IBonusTemplate
    {
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

    public record Combat(int level, @SerializedName("crit_chance") double critChance) implements IBonusTemplate
    {
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

    public record Enchanting(int level, double intelligence, @SerializedName("ability_damage") double abilityDamage) implements IBonusTemplate
    {
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

    public record Alchemy(int level, double intelligence) implements IBonusTemplate
    {
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

    public record Taming(int level, @SerializedName("pet_luck") double petLuck) implements IBonusTemplate
    {
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

        Type(String name)
        {
            this(name, ItemStack.EMPTY);
        }

        Type(String name, ItemLike item)
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

    public record Info(String name, double currentXp, int xpRequired, int currentLvl, double skillProgress, boolean reachLimit) {}
}