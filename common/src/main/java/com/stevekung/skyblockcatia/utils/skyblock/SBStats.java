package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public class SBStats
{
    private Component component;
    private String name;
    private final double value;
    private String valueString;

    public SBStats(Component component, double value)
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

    public Component getName()
    {
        if (this.component != null)
        {
            return this.component;
        }
        return TextComponentUtils.component(StringUtil.isNullOrEmpty(this.name) ? "" : this.name);
    }

    public double getValue()
    {
        return this.value;
    }

    public String getValueByString()
    {
        if (this.name == null || this.name.startsWith(ChatFormatting.YELLOW.toString()))
        {
            return "";
        }
        else if (!StringUtil.isNullOrEmpty(this.valueString))
        {
            return this.valueString;
        }
        return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.value);
    }
}