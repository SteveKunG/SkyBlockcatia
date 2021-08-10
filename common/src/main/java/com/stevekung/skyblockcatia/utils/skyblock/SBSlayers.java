package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public record SBSlayers(Map<String, int[]> leveling, com.stevekung.skyblockcatia.utils.skyblock.SBSlayers.Bonus bonus, Map<Integer, Integer> price)
{
    public static SBSlayers SLAYERS;

    public static void getSlayers()
    {
        SLAYERS = SkyBlockcatia.GSON.fromJson(DataUtils.getData("slayers.json"), SBSlayers.class);
    }

    public record Bonus(Zombie[] zombie, Spider[] spider, Wolf[] wolf, Enderman[] enderman) {}

    public record Zombie(int level, double health) implements IBonusTemplate
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

    public record Spider(int level, @SerializedName("crit_chance") double critChance, @SerializedName("crit_damage") double critDamage) implements IBonusTemplate
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

        @Override
        public double getCritDamage()
        {
            return this.critDamage;
        }
    }

    public record Wolf(int level, double health, double speed, @SerializedName("crit_damage") double critDamage) implements IBonusTemplate
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

    public record Enderman(int level, double health, double intelligence) implements IBonusTemplate
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
        public double getIntelligence()
        {
            return this.intelligence;
        }
    }

    public enum Type
    {
        UNKNOWN("Unknown"),
        ZOMBIE("Zombie"),
        SPIDER("Spider"),
        WOLF("Wolf"),
        ENDERMAN("Enderman");

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
            for (var type : values())
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