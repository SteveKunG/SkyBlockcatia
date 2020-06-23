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
        else if (this.name.contains("Race"))
        {
            double seconds = this.value / 1000.0D;
            return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(seconds) + " seconds";
        }
        return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.value);
    }

    public enum Type
    {
        KILLS, DEATHS, OTHERS;
    }
}