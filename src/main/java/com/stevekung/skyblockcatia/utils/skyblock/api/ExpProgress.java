package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.BufferedReader;

import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

public class ExpProgress
{
    private final int level;
    private final double xp;
    public static ExpProgress[] SKILL;
    public static ExpProgress[] ZOMBIE_SLAYER;
    public static ExpProgress[] SPIDER_SLAYER;
    public static ExpProgress[] WOLF_SLAYER;
    public static ExpProgress[] RUNECRAFTING;
    public static ExpProgress[] PET_COMMON;
    public static ExpProgress[] PET_UNCOMMON;
    public static ExpProgress[] PET_RARE;
    public static ExpProgress[] PET_EPIC;
    public static ExpProgress[] PET_LEGENDARY;
    public static ExpProgress[] DUNGEON;

    public ExpProgress(int level, double xp)
    {
        this.level = level;
        this.xp = xp;
    }

    public int getLevel()
    {
        return this.level;
    }

    public double getXp()
    {
        return this.xp;
    }

    public static ExpProgress[] getXpProgressFromRemote(Type type) throws Exception
    {
        BufferedReader in = DataGetter.get("api/exp_progress/" + type + ".json");
        return TextComponentUtils.GSON.fromJson(in, ExpProgress[].class);
    }

    public enum Type
    {
        SKILL,
        ZOMBIE_SLAYER,
        SPIDER_SLAYER,
        WOLF_SLAYER,
        RUNECRAFTING,
        PET_0,
        PET_1,
        PET_2,
        PET_3,
        PET_4,
        DUNGEON;

        @Override
        public String toString()
        {
            return this.name().toLowerCase();
        }
    }
}