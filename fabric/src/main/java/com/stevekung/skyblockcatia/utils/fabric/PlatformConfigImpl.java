package com.stevekung.skyblockcatia.utils.fabric;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaFabric;

public class PlatformConfigImpl
{
    public static String getApiKey()
    {
        return SkyBlockcatiaFabric.CONFIG.getConfig().hypixelApiKey;
    }

    public static void setApiKey(String key)
    {
        SkyBlockcatiaFabric.CONFIG.getConfig().hypixelApiKey = key;
    }

    public static boolean getDisableCameraEffect()
    {
        return SkyBlockcatiaFabric.CONFIG.getConfig().disableHurtCameraEffect;
    }

    public static boolean getSkinRenderingFix()
    {
        return SkyBlockcatiaFabric.CONFIG.getConfig().enableSkinRenderingFix;
    }

    public static boolean getChatInContainerScreen()
    {
        return SkyBlockcatiaFabric.CONFIG.getConfig().enableChatInContainerScreen;
    }
}