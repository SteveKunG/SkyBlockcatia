package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class SBSlayers
{
    public static SBSlayers SLAYERS;

    private final Map<String, int[]> leveling;
    private final Bonus bonus;

    public SBSlayers(Map<String, int[]> leveling, Bonus bonus)
    {
        this.leveling = leveling;
        this.bonus = bonus;
    }

    public Map<String, int[]> getLeveling()
    {
        return this.leveling;
    }

    public Bonus getBonus()
    {
        return this.bonus;
    }

    public static void getSlayers()
    {
        SLAYERS = TextComponentUtils.GSON.fromJson(DataUtils.getData("slayers.json"), SBSlayers.class);
    }

    public static class Bonus
    {
        private final Zombie[] zombie;
        private final Spider[] spider;
        private final Wolf[] wolf;

        public Bonus(Zombie[] zombie, Spider[] spider, Wolf[] wolf)
        {
            this.zombie = zombie;
            this.spider = spider;
            this.wolf = wolf;
        }

        public Zombie[] getZombie()
        {
            return this.zombie;
        }

        public Spider[] getSpider()
        {
            return this.spider;
        }

        public Wolf[] getWolf()
        {
            return this.wolf;
        }
    }

    public static class Zombie implements IBonusTemplate
    {
        private final int level;
        private final double health;

        public Zombie(int level, double health)
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

    public static class Spider implements IBonusTemplate
    {
        private final int level;
        @SerializedName("crit_chance")
        private final double critChance;
        @SerializedName("crit_damage")
        private final double critDamage;

        public Spider(int level, double critChance, double critDamage)
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

    public static class Wolf implements IBonusTemplate
    {
        private final int level;
        private final double health;
        private final double speed;
        @SerializedName("crit_damage")
        private final double critDamage;

        public Wolf(int level, double health, double speed, double critDamage)
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

    public enum Type
    {
        UNKNOWN("Unknown"),
        ZOMBIE("Zombie"),
        SPIDER("Spider"),
        WOLF("Wolf");

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }

        public static Type getSlayerByName(String name)
        {
            for (Type type : values())
            {
                if (name.equals(type.name()))
                {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

    public enum Drops
    {
        TARANTULA_WEB(TextComponentUtils.formatted("Tarantula Web", ChatFormatting.GREEN), Items.STRING),
        REVENANT_FLESH(TextComponentUtils.formatted("Revenant Flesh", ChatFormatting.GREEN), Items.ROTTEN_FLESH),
        WOLF_TOOTH(TextComponentUtils.formatted("Wolf Tooth", ChatFormatting.GREEN), Items.GHAST_TEAR);

        private final Component displayName;
        private final ItemLike baseItem;

        Drops(Component displayName, ItemLike baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public Component getDisplayName()
        {
            return this.displayName;
        }

        public ItemLike getBaseItem()
        {
            return this.baseItem;
        }
    }
}