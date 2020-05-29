package com.stevekung.skyblockcatia.integration;

import java.lang.reflect.Field;

import com.stevekung.indicatia.config.ExtendedConfig;
import com.stevekung.indicatia.gui.screen.IndicatiaChatScreen.ChatMode;

import net.minecraft.util.text.TextFormatting;

public class IndicatiaIntegration
{
    public static final ChatMode SKYBLOCK_COOP = ChatMode.create("SKYBLOCK_COOP", "menu.chat_mode.sb_coop_chat", TextFormatting.AQUA, 31, 98, 28, 20, "COOP", "/cc");

    public static String sendMessage(String defaultValue)
    {
        if (ExtendedConfig.INSTANCE.chatMode == SKYBLOCK_COOP.ordinal())
        {
            try
            {
                Field field = SKYBLOCK_COOP.getClass().getDeclaredField("command");
                field.setAccessible(true);
                return field.get(SKYBLOCK_COOP) + " " + defaultValue;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return defaultValue;
            }
        }
        else
        {
            return defaultValue;
        }
    }

    public static void savePartyChat()
    {
        ExtendedConfig.INSTANCE.chatMode = 0;
        ExtendedConfig.INSTANCE.save();
    }
}