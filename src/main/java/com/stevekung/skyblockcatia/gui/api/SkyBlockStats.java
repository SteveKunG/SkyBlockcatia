package com.stevekung.skyblockcatia.gui.api;

import java.text.DecimalFormat;

import net.minecraft.util.EnumChatFormatting;

public class SkyBlockStats
{
    private String name;
    private final double value;
    private static final DecimalFormat FORMAT = new DecimalFormat("#,###.#");

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
        else if (this.name.contains("Race") || this.name.contains("Best Time"))
        {
            return String.format("%1$TM:%1$TS.%1$TL", (long)this.value);
        }
        return FORMAT.format(this.value);
    }

    public enum Type
    {
        KILLS, DEATHS, OTHERS;
    }
}