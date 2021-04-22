package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Arrays;
import java.util.Comparator;

import com.stevekung.stevekungslib.utils.ColorUtils;
import net.minecraft.ChatFormatting;

public enum SBRarity
{
    COMMON("COMMON", ChatFormatting.WHITE, ColorUtils.toFloatArray(255, 255, 255)),
    UNCOMMON("UNCOMMON", ChatFormatting.GREEN, ColorUtils.toFloatArray(85, 255, 85)),
    RARE("RARE", ChatFormatting.BLUE, ColorUtils.toFloatArray(85, 85, 255)),
    EPIC("EPIC", ChatFormatting.DARK_PURPLE, ColorUtils.toFloatArray(170, 0, 170)),
    LEGENDARY("LEGENDARY", ChatFormatting.GOLD, ColorUtils.toFloatArray(255, 170, 0)),
    MYTHIC("MYTHIC", ChatFormatting.LIGHT_PURPLE, ColorUtils.toFloatArray(255, 85, 255)),
    SUPREME("SUPREME", ChatFormatting.DARK_RED, ColorUtils.toFloatArray(170, 0, 0)),
    SPECIAL("SPECIAL", ChatFormatting.RED, ColorUtils.toFloatArray(255, 85, 85)),
    VERY_SPECIAL("VERY SPECIAL", ChatFormatting.RED, ColorUtils.toFloatArray(170, 0, 0));

    private static final SBRarity[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(SBRarity::ordinal)).toArray(SBRarity[]::new);
    private final String name;
    private final ChatFormatting baseColor;
    private final float[] colorToRender;

    static
    {
        for (SBRarity rarity : values())
        {
            VALUES[rarity.ordinal()] = rarity;
        }
    }

    SBRarity(String name, ChatFormatting baseColor, float[] colorToRender)
    {
        this.name = name;
        this.baseColor = baseColor;
        this.colorToRender = colorToRender;
    }

    public String getName()
    {
        return this.name;
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