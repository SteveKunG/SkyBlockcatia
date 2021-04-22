package com.stevekung.skyblockcatia.utils;

import me.shedaniel.architectury.annotations.ExpectPlatform;

public class PlatformConfig
{
    @ExpectPlatform
    public static String getApiKey()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static void setApiKey(String key)
    {
        throw new Error();
    }

    @ExpectPlatform
    public static boolean getDisableCameraEffect()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static boolean getSkinRenderingFix()
    {
        throw new Error();
    }

    @ExpectPlatform
    public static boolean getChatInContainerScreen()
    {
        throw new Error();
    }
}