package com.stevekung.skyblockcatia.gui.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class ExpProgress
{
    private final int level;
    private final float xp;
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
    private static final Gson GSON = new Gson();

    public ExpProgress(int level, float xp)
    {
        this.level = level;
        this.xp = xp;
    }

    public int getLevel()
    {
        return this.level;
    }

    public float getXp()
    {
        return this.xp;
    }

    public static ExpProgress[] getXpProgressFromRemote(Type type) throws IOException
    {
        URL url = new URL("https://raw.githubusercontent.com/SteveKunG/Indicatia/1.8.9_skyblock/api/exp_progress/" + type + ".json");
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        return GSON.fromJson(in, ExpProgress[].class);
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
        PET_4;

        @Override
        public String toString()
        {
            return this.name().toLowerCase();
        }
    }
}