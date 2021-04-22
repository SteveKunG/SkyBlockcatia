package com.stevekung.skyblockcatia.utils;

import me.shedaniel.architectury.platform.Platform;

public class CompatibilityUtils
{
    public static boolean isSkyblockAddonsLoaded;

    public static void init()
    {
        isSkyblockAddonsLoaded = Platform.isModLoaded("skyblockaddons");
    }
}