package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

public class SBSlayers
{
    private static final Gson GSON = new Gson();
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

    public static void getSlayers() throws IOException
    {
        SLAYERS = GSON.fromJson(DataUtils.getData("slayers.json"), SBSlayers.class);
    }

    public class Bonus
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

    public class Zombie implements IBonusTemplate
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

    public class Spider implements IBonusTemplate
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

    public class Wolf implements IBonusTemplate
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
    }

    public enum Drops
    {
        TARANTULA_WEB(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Tarantula Web", Items.string),
        REVENANT_FLESH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Revenant Flesh", Items.rotten_flesh),
        WOLF_TOOTH(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Wolf Tooth", Items.ghast_tear);

        private final String displayName;
        private final Item baseItem;

        Drops(String displayName, Item baseItem)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public Item getBaseItem()
        {
            return this.baseItem;
        }
    }
}