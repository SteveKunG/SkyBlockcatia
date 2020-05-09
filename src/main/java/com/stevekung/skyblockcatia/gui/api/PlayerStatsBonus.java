package com.stevekung.skyblockcatia.gui.api;

import java.io.BufferedReader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.CurlExecutor;

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
    public static PlayerStatsBonus.ZombieSlayer[] ZOMBIE_SLAYER;
    public static PlayerStatsBonus.SpiderSlayer[] SPIDER_SLAYER;
    public static PlayerStatsBonus.WolfSlayer[] WOLF_SLAYER;
    public static PlayerStatsBonus.FairySouls[] FAIRY_SOULS;
    public static PlayerStatsBonus.PetsScore[] PETS_SCORE;
    private static final Gson GSON = new Gson();

    public static void getBonusFromRemote(Type type) throws Exception
    {
        BufferedReader in = CurlExecutor.execute("api/stats_bonuses/" + type.getPath() + "/" + type.toString() + ".json");

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
        case FAIRY_SOULS:
            FAIRY_SOULS = GSON.fromJson(in, PlayerStatsBonus.FairySouls[].class);
            break;
        case TAMING:
            TAMING = GSON.fromJson(in, PlayerStatsBonus.Taming[].class);
            break;
        case PETS_SCORE:
            PETS_SCORE = GSON.fromJson(in, PlayerStatsBonus.PetsScore[].class);
            break;
        default:
            break;
        }
    }

    public class Farming implements IBonusTemplate
    {
        private final int level;
        private final int health;

        public Farming(int level, int health)
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
        public int getHealth()
        {
            return this.health;
        }
    }

    public class Foraging implements IBonusTemplate
    {
        private final int level;
        private final int strength;

        public Foraging(int level, int strength)
        {
            this.level = level;
            this.strength = strength;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public int getStrength()
        {
            return this.strength;
        }
    }

    public class Mining implements IBonusTemplate
    {
        private final int level;
        private final int defense;

        public Mining(int level, int defense)
        {
            this.level = level;
            this.defense = defense;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public int getDefense()
        {
            return this.defense;
        }
    }

    public class Fishing implements IBonusTemplate
    {
        private final int level;
        private final int health;

        public Fishing(int level, int health)
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
        public int getHealth()
        {
            return this.health;
        }
    }

    public class Combat implements IBonusTemplate
    {
        private final int level;
        @SerializedName("crit_chance")
        private final int critChance;

        public Combat(int level, int critChance)
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
        public int getCritChance()
        {
            return this.critChance;
        }
    }

    public class Enchanting implements IBonusTemplate
    {
        private final int level;
        private final int intelligence;

        public Enchanting(int level, int intelligence)
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
        public int getIntelligence()
        {
            return this.intelligence;
        }
    }

    public class Alchemy implements IBonusTemplate
    {
        private final int level;
        private final int intelligence;

        public Alchemy(int level, int intelligence)
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
        public int getIntelligence()
        {
            return this.intelligence;
        }
    }

    public class Taming implements IBonusTemplate
    {
        private final int level;
        @SerializedName("pet_luck")
        private final int petLuck;

        public Taming(int level, int petLuck)
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
        public int getPetLuck()
        {
            return this.petLuck;
        }
    }

    public class ZombieSlayer implements IBonusTemplate
    {
        private final int level;
        private final int health;

        public ZombieSlayer(int level, int health)
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
        public int getHealth()
        {
            return this.health;
        }
    }

    public class SpiderSlayer implements IBonusTemplate
    {
        private final int level;
        @SerializedName("crit_chance")
        private final int critChance;
        @SerializedName("crit_damage")
        private final int critDamage;

        public SpiderSlayer(int level, int critChance, int critDamage)
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
        public int getCritChance()
        {
            return this.critChance;
        }

        @Override
        public int getCritDamage()
        {
            return this.critDamage;
        }
    }

    public class WolfSlayer implements IBonusTemplate
    {
        private final int level;
        private final int health;
        private final int speed;
        @SerializedName("crit_damage")
        private final int critDamage;

        public WolfSlayer(int level, int health, int speed, int critDamage)
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
        public int getHealth()
        {
            return this.health;
        }

        @Override
        public int getSpeed()
        {
            return this.speed;
        }

        @Override
        public int getCritDamage()
        {
            return this.critDamage;
        }
    }

    public class FairySouls implements IBonusTemplate
    {
        private final int count;
        private final int health;
        private final int defense;
        private final int strength;
        private final int speed;

        public FairySouls(int count, int health, int defense, int strength, int speed)
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
        public int getHealth()
        {
            return this.health;
        }

        @Override
        public int getDefense()
        {
            return this.defense;
        }

        @Override
        public int getStrength()
        {
            return this.strength;
        }

        @Override
        public int getSpeed()
        {
            return this.speed;
        }
    }

    public class PetsScore
    {
        private final int score;
        @SerializedName("magic_find")
        private final int magicFind;

        public PetsScore(int score, int magicFind)
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

    public interface IBonusTemplate
    {
        default int getLevel()
        {
            return 0;
        }

        default int getHealth()
        {
            return 0;
        }

        default int getDefense()
        {
            return 0;
        }

        default int getTrueDefense()
        {
            return 0;
        }

        default int getStrength()
        {
            return 0;
        }

        default int getSpeed()
        {
            return 0;
        }

        default int getCritChance()
        {
            return 0;
        }

        default int getCritDamage()
        {
            return 0;
        }

        default int getIntelligence()
        {
            return 0;
        }

        default int getSeaCreatureChance()
        {
            return 0;
        }

        default int getMagicFind()
        {
            return 0;
        }

        default int getPetLuck()
        {
            return 0;
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
        ZOMBIE_SLAYER("slayer"),
        SPIDER_SLAYER("slayer"),
        WOLF_SLAYER("slayer"),
        FAIRY_SOULS("misc"),
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
            return this.name().toLowerCase();
        }

        public String getPath()
        {
            return this.path;
        }
    }
}