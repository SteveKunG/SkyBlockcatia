package com.stevekung.skyblockcatia.utils.fabric;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaFabric;

public class PlatformConfigImpl
{
    public static String getApiKey()
    {
        return SkyBlockcatiaFabric.CONFIG.general.hypixelApiKey;
    }

    public static void setApiKey(String key)
    {
        SkyBlockcatiaFabric.CONFIG.general.hypixelApiKey = key;
    }

    public static boolean getDisableCameraEffect()
    {
        return SkyBlockcatiaFabric.CONFIG.general.disableHurtCameraEffect;
    }

    public static boolean getSkinRenderingFix()
    {
        return SkyBlockcatiaFabric.CONFIG.general.enableSkinRenderingFix;
    }

    public static boolean getChatInContainerScreen()
    {
        return SkyBlockcatiaFabric.CONFIG.general.enableChatInContainerScreen;
    }
}