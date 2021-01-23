package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class SBStats
{
    private ITextComponent component;
    private String name;
    private double value;
    private String valueString;

    public SBStats(ITextComponent component, double value)
    {
        this.component = component;
        this.value = value;
    }

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

    public ITextComponent getName()
    {
        if (this.component != null)
        {
            return this.component;
        }
        return TextComponentUtils.component(StringUtils.isNullOrEmpty(this.name) ? "" : this.name);
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
        else if (!StringUtils.isNullOrEmpty(this.valueString))
        {
            return this.valueString;
        }
        return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.value);
    }
}