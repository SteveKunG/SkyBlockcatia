package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.utils.ModDecimalFormat;

import net.minecraft.util.EnumChatFormatting;

public class SBStats
{
    private String name;
    private final double value;
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###.#");

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
        if (this.name == null || this.name.startsWith(EnumChatFormatting.YELLOW.toString()))
        {
            return "";
        }
        else if (this.name.contains("Race") || this.name.contains("No Return") || this.name.contains("With Return"))
        {
            return String.format("%1$TM:%1$TS.%1$TL", (long)this.value);
        }
        return FORMAT.format(this.value);
    }
}