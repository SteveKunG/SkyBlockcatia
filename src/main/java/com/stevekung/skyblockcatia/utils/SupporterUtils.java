package com.stevekung.skyblockcatia.utils;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.handler.ClientEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

public class SupporterUtils
{
    public static String getSpecialColor(String text)
    {
        Color rainbowColor = new Color(Color.HSBtoRGB(ClientEventHandler.rainbowTicks % 360 / 360F, 0.9F, 1F));
        String rainbowRGB = rainbowColor.getRed() + "," + rainbowColor.getGreen() + "," + rainbowColor.getBlue();

        if (SkyBlockEventHandler.isSkyBlock)
        {
            // well, im not pleased to do this, but as they requested and they really want it. so tysm for $50!
            if (text.contains("§6[MVP§d++§6] iEliteNerdy"))
            {
                return text.replace("§6[MVP§d++§6] iEliteNerdy", "§6[" + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "NERD§d++§6] " + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "iEliteNerdy");
            }
            else if (text.contains("§r§6[MVP§r§d++§r§6] iEliteNerdy"))
            {
                return text.replace("§r§6[MVP§r§d++§r§6] iEliteNerdy", "§6[" + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "NERD§d++§6] " + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "iEliteNerdy");
            }
            else if (text.contains("§r§6§6[MVP§r§d§d++§r§6§6] iEliteNerdy"))
            {
                return text.replace("§r§6§6[MVP§r§d§d++§r§6§6] iEliteNerdy", "§6[" + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "NERD§d++§6] " + ColorUtils.stringToRGB(rainbowRGB).toColoredFont() + "iEliteNerdy");
            }
            else if (text.contains("iEliteNerdy"))
            {
                return replaceSupportersName(text, "iEliteNerdy", rainbowRGB);
            }

            if (SkyBlockcatiaSettings.INSTANCE.supportersFancyColor)
            {
                for (String name : SkyBlockcatiaMod.SUPPORTERS_NAME)
                {
                    if (text.contains(name))
                    {
                        return replaceSupportersName(text, name, "36,224,186");
                    }
                }
            }
        }
        return text;
    }

    public static String replaceSupportersName(String text, String name, String color)
    {
        String namePatt = "(?:(?:\\u00a7[0-9a-fbr])\\B(?:" + name + ")\\b)|(?:\\u00a7[rb]" + name + "\\u00a7r)|\\b" + name + "\\b";
        Pattern prevColor = Pattern.compile("(?:.*\\B(?:(?<color>\\u00a7[0-9a-fbr])" + name + ")\\b.*)");
        Matcher prevColorMat = prevColor.matcher(text);

        if (prevColorMat.matches())
        {
            return text.replaceAll(namePatt, ColorUtils.stringToRGB(color).toColoredFont() + name + prevColorMat.group("color"));
        }
        return text.replaceAll(namePatt, ColorUtils.stringToRGB(color).toColoredFont() + name + ColorUtils.stringToRGB("255,255,255").toColoredFont());
    }
}