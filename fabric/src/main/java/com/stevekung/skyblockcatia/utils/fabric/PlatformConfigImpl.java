package com.stevekung.skyblockcatia.utils.fabric;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaFabricMod;

public class PlatformConfigImpl
{
    public static String getApiKey()
    {
        return SkyBlockcatiaFabricMod.CONFIG.getConfig().hypixelApiKey;
    }

    public static void setApiKey(String key)
    {
        SkyBlockcatiaFabricMod.CONFIG.getConfig().hypixelApiKey = key;
    }

    public static boolean getDisableCameraEffect()
    {
        return SkyBlockcatiaFabricMod.CONFIG.getConfig().disableHurtCameraEffect;
    }

    public static boolean getSkinRenderingFix()
    {
        return SkyBlockcatiaFabricMod.CONFIG.getConfig().enableSkinRenderingFix;
    }

    public static boolean getChatInContainerScreen()
    {
        return SkyBlockcatiaFabricMod.CONFIG.getConfig().enableChatInContainerScreen;
    }
}