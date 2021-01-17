package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.BufferedReader;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataGetter;

public class PlayerStatsBonus
{
    public static PlayerStatsBonus.Farming[] FARMING;
    public static PlayerStatsBonus.Foraging[] FORAGING;
    public static PlayerStatsBonus.Mining[] MINING;
    public static PlayerStatsBonus.Fishing[] FISHING;
    public static PlayerStatsBonus.Combat[] COMBAT;
    public static PlayerStatsBonus.Enchanting[] ENCHANTING;
    public static PlayerStatsBonus.Alchemy[] ALCHEMY;
    public static PlayerStatsBonus.Taming[] TAMING;
    public static PlayerStatsBonus.CatacombsDungeon[] CATACOMBS_DUNGEON;
    public static PlayerStatsBonus.ZombieSlayer[] ZOMBIE_SLAYER;
    public static PlayerStatsBonus.SpiderSlayer[] SPIDER_SLAYER;
    public static PlayerStatsBonus.WolfSlayer[] WOLF_SLAYER;
    public static PlayerStatsBonus.PetsScore[] PETS_SCORE;
    private static final Gson GSON = new Gson();

    public static void getBonusFromRemote(Type type) throws Exception
    {
        BufferedReader in = DataGetter.get("api/stats_bonuses/" + type.getPath() + "/" + type.toString() + ".json");

        switch (type)
        {
        case FARMING:
            FARMING = GSON.fromJson(in, PlayerStatsBonus.Farming[].class);
            break;
        case FORAGING:
            FORAGING = GSON.fromJson(in, PlayerStatsBonus.Foraging[].class);
            break;
        case MINING:
            MINING = GSON.fromJson(in, PlayerStatsBonus.Mining[].class);
            break;
        case FISHING:
            FISHING = GSON.fromJson(in, PlayerStatsBonus.Fishing[].class);
            break;
        case COMBAT:
            COMBAT = GSON.fromJson(in, PlayerStatsBonus.Combat[].class);
            break;
        case ENCHANTING:
            ENCHANTING = GSON.fromJson(in, PlayerStatsBonus.Enchanting[].class);
            break;
        case ALCHEMY:
            ALCHEMY = GSON.fromJson(in, PlayerStatsBonus.Alchemy[].class);
            break;
        case ZOMBIE_SLAYER:
            ZOMBIE_SLAYER = GSON.fromJson(in, PlayerStatsBonus.ZombieSlayer[].class);
            break;
        case SPIDER_SLAYER:
            SPIDER_SLAYER = GSON.fromJson(in, PlayerStatsBonus.SpiderSlayer[].class);
            break;
        case WOLF_SLAYER:
            WOLF_SLAYER = GSON.fromJson(in, PlayerStatsBonus.WolfSlayer[].class);
            break;
        case TAMING:
            TAMING = GSON.fromJson(in, PlayerStatsBonus.Taming[].class);
            break;
        case PETS_SCORE:
            PETS_SCORE = GSON.fromJson(in, PlayerStatsBonus.PetsScore[].class);
            break;
        case CATACOMBS_DUNGEON:
            CATACOMBS_DUNGEON = GSON.fromJson(in, PlayerStatsBonus.CatacombsDungeon[].class);
            break;
        default:
            break;
        }
    }

    public class Farming implements IBonusTemplate
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

    public class Foraging implements IBonusTemplate
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

    public class Mining implements IBonusTemplate
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

    public class Fishing implements IBonusTemplate
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

    public class Combat implements IBonusTemplate
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

    public class Enchanting implements IBonusTemplate
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

    public class Alchemy implements IBonusTemplate
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

    public class Taming implements IBonusTemplate
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

    public class CatacombsDungeon implements IBonusTemplate
    {
        private final int level;
        private final double health;

        public CatacombsDungeon(int level, double health)
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

    public class ZombieSlayer implements IBonusTemplate
    {
        private final int level;
        private final double health;

        public ZombieSlayer(int level, double health)
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

    public class SpiderSlayer implements IBonusTemplate
    {
        private final int level;
        @SerializedName("crit_chance")
        private final double critChance;
        @SerializedName("crit_damage")
        private final double critDamage;

        public SpiderSlayer(int level, double critChance, double critDamage)
        {
            this.level = level;
            this.critChance = critChance;
            this.critDamage = critDamage;
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

        @Override
        public double getCritDamage()
        {
            return this.critDamage;
        }
    }

    public class WolfSlayer implements IBonusTemplate
    {
        private final int level;
        private final double health;
        private final double speed;
        @SerializedName("crit_damage")
        private final double critDamage;

        public WolfSlayer(int level, double health, double speed, double critDamage)
        {
            this.level = level;
            this.health = health;
            this.speed = speed;
            this.critDamage = critDamage;
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
        public double getSpeed()
        {
            return this.speed;
        }

        @Override
        public double getCritDamage()
        {
            return this.critDamage;
        }
    }

    public class FairySouls implements IBonusTemplate
    {
        private final int count;
        private final double health;
        private final double defense;
        private final double strength;
        private final double speed;

        public FairySouls(int count, double health, double defense, double strength, double speed)
        {
            this.count = count;
            this.health = health;
            this.defense = defense;
            this.strength = strength;
            this.speed = speed;
        }

        public int getCount()
        {
            return this.count;
        }

        @Override
        public double getHealth()
        {
            return this.health;
        }

        @Override
        public double getDefense()
        {
            return this.defense;
        }

        @Override
        public double getStrength()
        {
            return this.strength;
        }

        @Override
        public double getSpeed()
        {
            return this.speed;
        }
    }

    public class PetsScore implements IBonusTemplate
    {
        private final int score;
        @SerializedName("magic_find")
        private final double magicFind;

        public PetsScore(int score, double magicFind)
        {
            this.score = score;
            this.magicFind = magicFind;
        }

        public int getScore()
        {
            return this.score;
        }

        @Override
        public double getMagicFind()
        {
            return this.magicFind;
        }
    }

    public enum Type
    {
        FARMING("skill"),
        FORAGING("skill"),
        MINING("skill"),
        FISHING("skill"),
        COMBAT("skill"),
        ENCHANTING("skill"),
        ALCHEMY("skill"),
        TAMING("skill"),
        CATACOMBS_DUNGEON("skill"),
        ZOMBIE_SLAYER("slayer"),
        SPIDER_SLAYER("slayer"),
        WOLF_SLAYER("slayer"),
        PETS_SCORE("misc");

        public static final Type[] VALUES = values();
        private final String path;

        private Type(String path)
        {
            this.path = path;
        }

        @Override
        public String toString()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String getPath()
        {
            return this.path;
        }
    }
}