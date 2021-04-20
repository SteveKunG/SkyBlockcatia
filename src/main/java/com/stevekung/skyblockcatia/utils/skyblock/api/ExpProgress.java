package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.BufferedReader;
import java.util.Locale;

import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

@Deprecated
public class ExpProgress
{
    private final int level;
    private final double xp;
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
        BufferedReader in = DataUtils.get("api/exp_progress/" + type + ".json");
        return TextComponentUtils.GSON.fromJson(in, ExpProgress[].class);
    }

    public enum Type
    {
        DUNGEON;

        @Override
        public String toString()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}