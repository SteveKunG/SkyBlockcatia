package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Arrays;
import java.util.Comparator;

import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.util.text.TextFormatting;

public enum SBRarity
{
    COMMON("COMMON", TextFormatting.WHITE, ColorUtils.toFloatArray(255, 255, 255)),
    UNCOMMON("UNCOMMON", TextFormatting.GREEN, ColorUtils.toFloatArray(85, 255, 85)),
    RARE("RARE", TextFormatting.BLUE, ColorUtils.toFloatArray(85, 85, 255)),
    EPIC("EPIC", TextFormatting.DARK_PURPLE, ColorUtils.toFloatArray(170, 0, 170)),
    LEGENDARY("LEGENDARY", TextFormatting.GOLD, ColorUtils.toFloatArray(255, 170, 0)),
    MYTHIC("MYTHIC", TextFormatting.LIGHT_PURPLE, ColorUtils.toFloatArray(255, 85, 255)),
    SUPREME("SUPREME", TextFormatting.DARK_RED, ColorUtils.toFloatArray(170, 0, 0)),
    SPECIAL("SPECIAL", TextFormatting.RED, ColorUtils.toFloatArray(255, 85, 85)),
    VERY_SPECIAL("VERY SPECIAL", TextFormatting.RED, ColorUtils.toFloatArray(170, 0, 0));

    private static final SBRarity[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(SBRarity::ordinal)).toArray(size -> new SBRarity[size]);
    private final String name;
    private final TextFormatting baseColor;
    private final float[] colorToRender;

    static
    {
        for (SBRarity rarity : values())
        {
            VALUES[rarity.ordinal()] = rarity;
        }
    }

    SBRarity(String name, TextFormatting baseColor, float[] colorToRender)
    {
        this.name = name;
        this.baseColor = baseColor;
        this.colorToRender = colorToRender;
    }

    public String getName()
    {
        return this.name;
    }

    public TextFormatting getBaseColor()
    {
        return this.baseColor;
    }

    public float[] getColorToRender()
    {
        return this.colorToRender;
    }

    public static SBRarity byBaseColor(String color)
    {
        for (SBRarity rarity : values())
        {
            if (rarity.baseColor.toString().equals(color))
            {
                return rarity;
            }
        }
        return null;
    }

    public SBRarity getNextRarity()
    {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}