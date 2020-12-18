package com.stevekung.skyblockcatia.utils;

import net.minecraftforge.fml.common.Loader;

public class CompatibilityUtils
{
    public static boolean isSkyblockAddonsLoaded = Loader.isModLoaded("skyblockaddons");
    public static boolean isIngameAccountSwitcherLoaded = Loader.isModLoaded("IngameAccountSwitcher");
    public static boolean isVanillaEnhancementsLoaded = Loader.isModLoaded("enhancements");
    public static boolean isPatcherLoaded = Loader.isModLoaded("patcher");
    public static boolean isTextOverflowScrollLoaded = Loader.isModLoaded("text_overflow_scroll");
    public static boolean isNotEnoughUpdatesLoaded = Loader.isModLoaded("notenoughupdates");

    // Patcher Compatibility
    private static Class<?> patcherConfig;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;

    // Not Enough Updates Compatibility
    private static Class<?> neuConfig;

    public static void init()
    {
        if (isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (isVanillaEnhancementsLoaded)
        {
            try
            {
                vanillaEnConfig = Class.forName("com.orangemarshall.enhancements.config.Config");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (isNotEnoughUpdatesLoaded)
        {
            try
            {
                neuConfig = Class.forName("io.github.moulberry.notenoughupdates.NotEnoughUpdates");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasInventoryFix()
    {
        if (isPatcherLoaded)
        {
            try
            {
                return patcherConfig.getDeclaredField("inventoryPosition").getBoolean(patcherConfig);
            }
            catch (Exception e) {}
        }
        if (isVanillaEnhancementsLoaded)
        {
            try
            {
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                return instance.getClass().getDeclaredField("fixInventory").getBoolean(instance);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (isNotEnoughUpdatesLoaded)
        {
            try
            {
                Object instance = neuConfig.getDeclaredField("INSTANCE").get(neuConfig);
                Object manager = instance.getClass().getDeclaredField("manager").get(instance);
                Object config = manager.getClass().getDeclaredField("config").get(manager);
                Object hidePotionEffect = config.getClass().getDeclaredField("hidePotionEffect").get(config);
                return (boolean)hidePotionEffect.getClass().getDeclaredField("value").get(hidePotionEffect);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean hasDisableEnchantmentGlint()
    {
        if (isPatcherLoaded)
        {
            try
            {
                return patcherConfig.getDeclaredField("disableEnchantmentGlint").getBoolean(patcherConfig);
            }
            catch (Exception e) {}
        }
        return false;
    }
}