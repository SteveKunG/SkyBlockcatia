package com.stevekung.skyblockcatia.integration;

import com.stevekung.indicatia.config.IndicatiaSettings;
import com.stevekung.indicatia.gui.screen.IndicatiaChatScreen;
import com.stevekung.stevekungslib.utils.CommonUtils;

import net.minecraft.util.text.TextFormatting;

public class IndicatiaIntegration
{
    public static final IndicatiaChatScreen.ChatMode SKYBLOCK_COOP = IndicatiaChatScreen.ChatMode.create("SKYBLOCK_COOP", "menu.chat_mode.sb_coop_chat", TextFormatting.AQUA, 31, 98, 28, 20, "COOP", "/cc");
    public static boolean otherPlayerIsland;

    public static void registerHandler()
    {
        CommonUtils.registerEventHandler(new RenderSkyBlockInfo());
    }

    public static String sendMessage(String defaultValue)
    {
        if (IndicatiaSettings.INSTANCE.chatMode == SKYBLOCK_COOP.ordinal())
        {
            return SKYBLOCK_COOP.command + " " + defaultValue;
        }
        else
        {
            return defaultValue;
        }
    }

    public static void savePartyChat()
    {
        IndicatiaSettings.INSTANCE.chatMode = 0;
        IndicatiaSettings.INSTANCE.save();
    }
}