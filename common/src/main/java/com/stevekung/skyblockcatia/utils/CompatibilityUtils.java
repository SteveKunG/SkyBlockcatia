package com.stevekung.skyblockcatia.utils;

import dev.architectury.platform.Platform;

public class CompatibilityUtils
{
    public static boolean isSkyblockAddonsLoaded;

    public static void init()
    {
        isSkyblockAddonsLoaded = Platform.isModLoaded("skyblockaddons");
    }
}