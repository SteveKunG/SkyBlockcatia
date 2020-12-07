package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.utils.ModDecimalFormat;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

public class SBStats
{
    private String name;
    private final double value;
    private String valueString;
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###.#");

    public SBStats(String name, double value)
    {
        this.name = name;
        this.value = value;
    }

    public SBStats(String name, String valueString)
    {
        this.name = name;
        this.value = 0;
        this.valueString = valueString;
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
        else if (!StringUtils.isNullOrEmpty(this.valueString))
        {
            return this.valueString;
        }
        return FORMAT.format(this.value);
    }
}