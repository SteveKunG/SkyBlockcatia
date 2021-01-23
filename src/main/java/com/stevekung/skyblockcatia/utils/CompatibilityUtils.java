package com.stevekung.skyblockcatia.utils;

import net.minecraftforge.fml.ModList;

public class CompatibilityUtils
{
    public static boolean isSkyblockAddonsLoaded;
    public static boolean isIndicatiaLoaded;

    public static void init()
    {
        isSkyblockAddonsLoaded = ModList.get().isLoaded("skyblockaddons");
        isIndicatiaLoaded = ModList.get().isLoaded("indicatia");
    }
}