package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.BufferedReader;
import java.util.Locale;

import com.google.gson.Gson;
import com.stevekung.skyblockcatia.utils.DataUtils;

@Deprecated
public class ExpProgress
{
    private final int level;
    private final double xp;
    public static ExpProgress[] DUNGEON;
    private static final Gson GSON = new Gson();

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
        BufferedReader in = DataUtils.get("api/exp_progress/" + type.toString().toLowerCase(Locale.ROOT) + ".json");
        return GSON.fromJson(in, ExpProgress[].class);
    }

    public enum Type
    {
        DUNGEON;
    }
}