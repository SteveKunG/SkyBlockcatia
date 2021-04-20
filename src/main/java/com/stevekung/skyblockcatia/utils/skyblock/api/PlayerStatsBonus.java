package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.BufferedReader;
import java.util.Locale;

import com.google.gson.Gson;
import com.stevekung.skyblockcatia.utils.DataUtils;

@Deprecated
public class PlayerStatsBonus
{
    public static PlayerStatsBonus.CatacombsDungeon[] CATACOMBS_DUNGEON;
    private static final Gson GSON = new Gson();

    public static void getBonusFromRemote(Type type) throws Exception
    {
        BufferedReader in = DataUtils.get("api/stats_bonuses/" + type.getPath() + "/" + type.toString() + ".json");

        switch (type)
        {
        case CATACOMBS_DUNGEON:
            CATACOMBS_DUNGEON = GSON.fromJson(in, PlayerStatsBonus.CatacombsDungeon[].class);
            break;
        default:
            break;
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

    public enum Type
    {
        CATACOMBS_DUNGEON("skill");

        public static final Type[] VALUES = values();
        private final String path;

        Type(String path)
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