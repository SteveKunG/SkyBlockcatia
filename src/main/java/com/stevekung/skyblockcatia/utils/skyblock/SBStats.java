package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.NumberUtils;

import net.minecraft.util.text.TextFormatting;

public class SBStats
{
    private String name;
    private final double value;

    public SBStats(String name, double value)
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
        if (this.name == null || this.name.startsWith(TextFormatting.YELLOW.toString()))
        {
            return "";
        }
        else if (this.name.contains("Race") || this.name.contains("Best Time"))
        {
            return String.format("%1$TM:%1$TS.%1$TL", (long)this.value);
        }
        return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.value);
    }

    public enum Type
    {
        KILLS, DEATHS, OTHERS;
    }
}