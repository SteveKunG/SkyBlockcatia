package com.stevekung.skyblockcatia.utils.forge;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

public class PlatformConfigImpl
{
    public static String getApiKey()
    {
        return SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get();
    }

    public static void setApiKey(String key)
    {
        SkyBlockcatiaConfig.GENERAL.hypixelApiKey.set(key);
    }

    public static boolean getDisableCameraEffect()
    {
        return SkyBlockcatiaConfig.GENERAL.disableHurtCameraEffect.get();
    }

    public static boolean getSkinRenderingFix()
    {
        return SkyBlockcatiaConfig.GENERAL.enableSkinRenderingFix.get();
    }

    public static boolean getChatInContainerScreen()
    {
        return SkyBlockcatiaConfig.GENERAL.enableChatInContainerScreen.get();
    }
}