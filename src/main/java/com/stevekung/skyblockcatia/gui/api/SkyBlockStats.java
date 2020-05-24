package com.stevekung.skyblockcatia.gui.api;

import java.text.DecimalFormat;

import net.minecraft.util.EnumChatFormatting;

public class SkyBlockStats
{
    private String name;
    private final double value;
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###,###.#");

    public SkyBlockStats(String name, double value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public double getValue()
    {
        return this.value;
    }

    public String getValueByString()
    {
        if (this.name == null || this.name.startsWith(EnumChatFormatting.YELLOW.toString()))
        {
            return "";
        }
        else if (this.name.contains("Race"))
        {
            double seconds = this.value / 1000.0D;
            return FORMAT.format(seconds) + " seconds";
        }
        return FORMAT.format(this.value);
    }

    public enum Type
    {
        KILLS, DEATHS, OTHERS;
    }
}