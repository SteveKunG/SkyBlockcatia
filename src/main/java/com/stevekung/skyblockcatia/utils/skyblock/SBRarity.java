package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Arrays;
import java.util.Comparator;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.util.EnumChatFormatting;

public enum SBRarity
{
    COMMON("COMMON", EnumChatFormatting.WHITE, ColorUtils.stringToRGB(SkyBlockcatiaConfig.commonRarityColor)),
    UNCOMMON("UNCOMMON", EnumChatFormatting.GREEN, ColorUtils.stringToRGB(SkyBlockcatiaConfig.uncommonRarityColor)),
    RARE("RARE", EnumChatFormatting.BLUE, ColorUtils.stringToRGB(SkyBlockcatiaConfig.rareRarityColor)),
    EPIC("EPIC", EnumChatFormatting.DARK_PURPLE, ColorUtils.stringToRGB(SkyBlockcatiaConfig.epicRarityColor)),
    LEGENDARY("LEGENDARY", EnumChatFormatting.GOLD, ColorUtils.stringToRGB(SkyBlockcatiaConfig.legendaryRarityColor)),
    MYTHIC("MYTHIC", EnumChatFormatting.LIGHT_PURPLE, ColorUtils.stringToRGB(SkyBlockcatiaConfig.mythicRarityColor)),
    SUPREME("SUPREME", EnumChatFormatting.DARK_RED, ColorUtils.stringToRGB(SkyBlockcatiaConfig.supremeRarityColor)),
    SPECIAL("SPECIAL", EnumChatFormatting.RED, ColorUtils.stringToRGB(SkyBlockcatiaConfig.specialRarityColor)),
    VERY_SPECIAL("VERY SPECIAL", EnumChatFormatting.RED, ColorUtils.stringToRGB(SkyBlockcatiaConfig.verySpecialRarityColor));

    private static final SBRarity[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(SBRarity::ordinal)).toArray(size -> new SBRarity[size]);
    private final String name;
    private final EnumChatFormatting baseColor;
    private final ColorUtils.RGB colorToRender;

    static
    {
        for (SBRarity rarity : values())
        {
            VALUES[rarity.ordinal()] = rarity;
        }
    }

    SBRarity(String name, EnumChatFormatting baseColor, ColorUtils.RGB colorToRender)
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