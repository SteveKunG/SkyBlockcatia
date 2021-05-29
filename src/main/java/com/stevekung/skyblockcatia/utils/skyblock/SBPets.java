package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.ModDecimalFormat;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SBPets
{
    private static final Gson GSON = new Gson();
    public static SBPets PETS;

    @SerializedName("held_item")
    private final HeldItem[] heldItem;
    private final Map<String, Integer> index;
    private final int[] leveling;
    private final Score[] score;
    private final Skin[] skin;
    private final Type[] type;

    public SBPets(HeldItem[] heldItem, Map<String, Integer> index, int[] leveling, Score[] score, Skin[] skin, Type[] type)
    {
        this.heldItem = heldItem;
        this.index = index;
        this.leveling = leveling;
        this.score = score;
        this.skin = skin;
        this.type = type;
    }

    public static void getPets() throws IOException
    {
        PETS = GSON.fromJson(DataUtils.getData("pets.json"), SBPets.class);
    }

    public HeldItem getHeldItemByName(String name)
    {
        for (HeldItem item : this.heldItem)
        {
            if (item.getType().equals(name))
            {
                return item;
            }
        }
        return null;
    }

    public Map<String, Integer> getIndex()
    {
        return this.index;
    }

    public int[] getLeveling()
    {
        return this.leveling;
    }

    public Score[] getScore()
    {
        return this.score;
    }

    public Skin[] getSkin()
    {
        return this.skin;
    }

    public Type getTypeByName(String name)
    {
        for (Type type : this.type)
        {
            if (type.getType().equals(name))
            {
                return type;
            }
        }
        return null;
    }

    public class Skin
    {
        private final String type;
        @SerializedName("displayName")
        private final String name;
        private final String color;
        private final String uuid;
        private final String texture;

        public Skin(String type, String name, String color, String uuid, String texture)
        {
            this.type = type;
            this.name = name;
            this.color = color;
            this.uuid = uuid;
            this.texture = texture;
        }

        public String getType()
        {
            return this.type;
        }

        public String getName()
        {
            return this.name;
        }

        public EnumChatFormatting getColor()
        {
            return EnumChatFormatting.getValueByName(this.color);
        }

        public String getUUID()
        {
            return this.uuid;
        }

        public String getTexture()
        {
            return this.texture;
        }
    }

    public enum Tier
    {
        COMMON(EnumChatFormatting.WHITE),
        UNCOMMON(EnumChatFormatting.GREEN),
        RARE(EnumChatFormatting.BLUE),
        EPIC(EnumChatFormatting.DARK_PURPLE),
        LEGENDARY(EnumChatFormatting.GOLD),
        MYTHIC(EnumChatFormatting.LIGHT_PURPLE);

        private static final Tier[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Tier::ordinal)).toArray(size -> new Tier[size]);
        private final EnumChatFormatting color;

        static
        {
            for (Tier rarity : values())
            {
                VALUES[rarity.ordinal()] = rarity;
            }
        }

        Tier(EnumChatFormatting color)
        {
            this.color = color;
        }

        public EnumChatFormatting getTierColor()
        {
            return this.color;
        }

        public Tier getNextRarity()
        {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
    }

    public class Type
    {
        private final String type;
        private final String skill;
        private final String uuid;
        private final String texture;
        private final Map<String, Stats> stats;
        private final Map<String, StatsPropertyArray> statsLore;
        private final Map<String, List<String>> lore;

        public Type(String type, String skill, String uuid, String texture, Map<String, Stats> stats, Map<String, StatsPropertyArray> statsLore, Map<String, List<String>> lore)
        {
            this.type = type;
            this.skill = skill;
            this.uuid = uuid;
            this.texture = texture;
            this.stats = stats;
            this.statsLore = statsLore;
            this.lore = lore;
        }

        public String getType()
        {
            return this.type;
        }

        public SBSkills.Type getSkill()
        {
            return SBSkills.Type.byName(this.skill);
        }

        public Map<String, Stats> getStats()
        {
            return this.stats;
        }

        public Map<String, StatsPropertyArray> getStatsLore()
        {
            return this.statsLore;
        }

        public Map<String, List<String>> getLore()
        {
            return this.lore;
        }

        public ItemStack getPetItem()
        {
            return RenderUtils.getSkullItemStack(this.uuid, this.texture);
        }
    }

    public class Stats
    {
        private final StatsProperty health;
        private final StatsProperty strength;
        @SerializedName("crit_damage")
        private final StatsProperty critDamage;
        @SerializedName("crit_chance")
        private final StatsProperty critChance;
        @SerializedName("attack_speed")
        private final StatsProperty attackSpeed;
        private final StatsProperty ferocity;
        private final StatsProperty defense;
        private final StatsProperty speed;
        private final StatsProperty intelligence;
        @SerializedName("sea_creature_chance")
        private final StatsProperty seaCreatureChance;
        @SerializedName("magic_find")
        private final StatsProperty magicFind;

        public Stats(StatsProperty health, StatsProperty strength, StatsProperty critDamage, StatsProperty critChance, StatsProperty attackSpeed, StatsProperty ferocity, StatsProperty defense, StatsProperty speed, StatsProperty intelligence, StatsProperty seaCreatureChance, StatsProperty magicFind)
        {
            this.health = health;
            this.strength = strength;
            this.critDamage = critDamage;
            this.critChance = critChance;
            this.attackSpeed = attackSpeed;
            this.ferocity = ferocity;
            this.defense = defense;
            this.speed = speed;
            this.intelligence = intelligence;
            this.seaCreatureChance = seaCreatureChance;
            this.magicFind = magicFind;
        }

        public StatsProperty getHealth()
        {
            return this.health;
        }

        public StatsProperty getStrength()
        {
            return this.strength;
        }

        public StatsProperty getCritDamage()
        {
            return this.critDamage;
        }

        public StatsProperty getCritChance()
        {
            return this.critChance;
        }

        public StatsProperty getFerocity()
        {
            return this.ferocity;
        }

        public StatsProperty getAttackSpeed()
        {
            return this.attackSpeed;
        }

        public StatsProperty getSpeed()
        {
            return this.speed;
        }

        public StatsProperty getDefense()
        {
            return this.defense;
        }

        public StatsProperty getIntelligence()
        {
            return this.intelligence;
        }

        public StatsProperty getSeaCreatureChance()
        {
            return this.seaCreatureChance;
        }

        public StatsProperty getMagicFind()
        {
            return this.magicFind;
        }
    }

    public class StatsProperty
    {
        private final double base;
        private final double multiply;
        @SerializedName("isPercent")
        private final boolean percent;

        public StatsProperty(double base, double multiply, boolean percent)
        {
            this.base = base;
            this.multiply = multiply;
            this.percent = percent;
        }

        public int getValue(int level)
        {
            return (int)(this.base + this.multiply * level);
        }

        public String getString(String type, int level)
        {
            int value = this.getValue(level);
            return EnumChatFormatting.RESET.toString() + EnumChatFormatting.GRAY + type + ": " + EnumChatFormatting.GREEN + (value < 0 ? "" : "+") + value + (this.percent ? "%" : "");
        }
    }

    public class StatsPropertyArray
    {
        private final double[] base;
        private final double[] multiply;
        @SerializedName("rounding_mode")
        private final String roundingMode;
        @SerializedName("display_mode")
        private final String displayMode;

        public StatsPropertyArray(double[] base, double[] multiply, String roundingMode, String displayMode)
        {
            this.base = base;
            this.multiply = multiply;
            this.roundingMode = roundingMode;
            this.displayMode = displayMode;
        }

        public double[] getBase()
        {
            return this.base;
        }

        public double[] getMultiply()
        {
            return this.multiply;
        }

        public String getRoundingMode()
        {
            return this.roundingMode;
        }

        public String getDisplayMode()
        {
            return this.displayMode;
        }
    }

    public class HeldItem
    {
        private final String type;
        private final String name;
        private final String color;
        private final boolean isUpgrade;
        private final List<String> lore;
        private final Stats stats;

        public HeldItem(String type, String name, String color, boolean isUpgrade, List<String> lore, Stats stats)
        {
            this.type = type;
            this.name = name;
            this.color = color;
            this.isUpgrade = isUpgrade;
            this.lore = lore;
            this.stats = stats;
        }

        public String getType()
        {
            return this.type;
        }

        public String getName()
        {
            return this.name;
        }

        public String getColor()
        {
            return this.color;
        }

        public boolean isUpgrade()
        {
            return this.isUpgrade;
        }

        public List<String> getLore()
        {
            return this.lore;
        }

        public Stats getStats()
        {
            return this.stats;
        }
    }

    public class Score
    {
        private final int score;
        @SerializedName("magic_find")
        private final int magicFind;

        public Score(int score, int magicFind)
        {
            this.score = score;
            this.magicFind = magicFind;
        }

        public int getScore()
        {
            return this.score;
        }

        public int getMagicFind()
        {
            return this.magicFind;
        }
    }

    public static class Info
    {
        private final int currentPetLevel;
        private final int nextPetLevel;
        private final double currentPetXp;
        private final int xpRequired;
        private final double petXp;
        private final int totalPetTypeXp;

        public Info(int currentPetLevel, int nextPetLevel, double currentPetXp, int xpRequired, double petXp, int totalPetTypeXp)
        {
            this.currentPetLevel = currentPetLevel;
            this.nextPetLevel = nextPetLevel;
            this.currentPetXp = currentPetXp;
            this.xpRequired = xpRequired;
            this.petXp = petXp;
            this.totalPetTypeXp = totalPetTypeXp;
        }

        public int getCurrentPetLevel()
        {
            return this.currentPetLevel;
        }

        public int getNextPetLevel()
        {
            return this.nextPetLevel;
        }

        public double getCurrentPetXp()
        {
            return this.currentPetXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public double getPetXp()
        {
            return this.petXp;
        }

        public int getTotalPetTypeXp()
        {
            return this.totalPetTypeXp;
        }

        public String getPercent()
        {
            if (this.xpRequired > 0)
            {
                double percent = this.currentPetXp * 100.0D / this.xpRequired;
                return new ModDecimalFormat("##.#").format(percent) + "%";
            }
            else
            {
                return EnumChatFormatting.AQUA.toString() + EnumChatFormatting.BOLD + "MAX LEVEL";
            }
        }
    }

    public static class Data
    {
        private final SBPets.Tier tier;
        private final int currentLevel;
        private final String name;
        private final boolean isActive;
        private final List<ItemStack> itemStack;

        public Data(SBPets.Tier tier, int currentLevel, String name, boolean isActive, List<ItemStack> itemStack)
        {
            this.tier = tier;
            this.currentLevel = currentLevel;
            this.name = name;
            this.isActive = isActive;
            this.itemStack = itemStack;
        }

        public SBPets.Tier getTier()
        {
            return this.tier;
        }

        public List<ItemStack> getItemStack()
        {
            return this.itemStack;
        }

        public int getCurrentLevel()
        {
            return this.currentLevel;
        }

        public String getName()
        {
            return this.name;
        }

        public boolean isActive()
        {
            return this.isActive;
        }
    }
}