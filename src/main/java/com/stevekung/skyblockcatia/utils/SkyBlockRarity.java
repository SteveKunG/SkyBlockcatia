package com.stevekung.skyblockcatia.utils;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.EnumChatFormatting;

public enum SkyBlockRarity
{
    COMMON("COMMON", EnumChatFormatting.WHITE, ColorUtils.stringToRGB("255,255,255")),
    UNCOMMON("UNCOMMON", EnumChatFormatting.GREEN, ColorUtils.stringToRGB("85,255,85")),
    RARE("RARE", EnumChatFormatting.BLUE, ColorUtils.stringToRGB("85,85,255")),
    EPIC("EPIC", EnumChatFormatting.DARK_PURPLE, ColorUtils.stringToRGB("170,0,170")),
    LEGENDARY("LEGENDARY", EnumChatFormatting.GOLD, ColorUtils.stringToRGB("255,170,0")),
    MYTHIC("MYTHIC", EnumChatFormatting.LIGHT_PURPLE, ColorUtils.stringToRGB("255,85,255")),
    SUPREME("SUPREME", EnumChatFormatting.DARK_RED, ColorUtils.stringToRGB("170,0,0")),
    SPECIAL("SPECIAL", EnumChatFormatting.RED, ColorUtils.stringToRGB("255,85,85")),
    VERY_SPECIAL("VERY SPECIAL", EnumChatFormatting.RED, ColorUtils.stringToRGB("170,0,0"));

    private static final SkyBlockRarity[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(SkyBlockRarity::ordinal)).toArray(size -> new SkyBlockRarity[size]);
    private final String name;
    private final EnumChatFormatting baseColor;
    private final ColorUtils.RGB colorToRender;

    static
    {
        for (SkyBlockRarity rarity : values())
        {
            VALUES[rarity.ordinal()] = rarity;
        }
    }

    private SkyBlockRarity(String name, EnumChatFormatting baseColor, ColorUtils.RGB colorToRender)
    {
        this.name = name;
        this.baseColor = baseColor;
        this.colorToRender = colorToRender;
    }

    public String getName()
    {
        return this.name;
    }

    public EnumChatFormatting getBaseColor()
    {
        return this.baseColor;
    }

    public ColorUtils.RGB getColorToRender()
    {
        return this.colorToRender;
    }

    public static SkyBlockRarity byBaseColor(String color)
    {
        for (SkyBlockRarity rarity : values())
        {
            if (rarity.baseColor.toString().equals(color))
            {
                return rarity;
            }
        }
        return null;
    }

    public SkyBlockRarity getNextRarity()
    {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}