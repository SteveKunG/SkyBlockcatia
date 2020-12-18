package com.stevekung.skyblockcatia.utils;

import java.lang.reflect.Field;

import net.minecraftforge.fml.common.Loader;

public class CompatibilityUtils
{
    public static final CompatibilityUtils INSTANCE = new CompatibilityUtils();
    public static boolean isSkyblockAddonsLoaded = Loader.isModLoaded("skyblockaddons");
    public static boolean isIngameAccountSwitcherLoaded = Loader.isModLoaded("IngameAccountSwitcher");
    public static boolean isVanillaEnhancementsLoaded = Loader.isModLoaded("enhancements");
    public static boolean isPatcherLoaded = Loader.isModLoaded("patcher");
    public static boolean isTextOverflowScrollLoaded = Loader.isModLoaded("text_overflow_scroll");
    public static boolean isNotEnoughUpdatesLoaded = Loader.isModLoaded("notenoughupdates");

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherInventoryPosition;
    private boolean disableEnchantmentGlint;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;
    private boolean vanillaEnFixInventory;

    // Not Enough Updates Compatibility
    private static Class<?> neuConfig;
    private boolean neuhidePotionEffect;

    public CompatibilityUtils()
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

    public boolean hasInventoryFix()
    {
        if (isPatcherLoaded)
        {
            try
            {
                Field inventoryPosition = patcherConfig.getDeclaredField("inventoryPosition");
                this.patcherInventoryPosition = inventoryPosition.getBoolean(patcherConfig);
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
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                Field fixInventory = instance.getClass().getDeclaredField("fixInventory");
                this.vanillaEnFixInventory = fixInventory.getBoolean(instance);
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
                this.neuhidePotionEffect = (boolean)hidePotionEffect.getClass().getDeclaredField("value").get(hidePotionEffect);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return this.vanillaEnFixInventory || this.patcherInventoryPosition || this.neuhidePotionEffect;
    }

    public boolean hasDisableEnchantmentGlint()
    {
        if (isPatcherLoaded)
        {
            try
            {
                Field disableEnchantmentGlint = patcherConfig.getDeclaredField("disableEnchantmentGlint");
                this.disableEnchantmentGlint = disableEnchantmentGlint.getBoolean(patcherConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return this.disableEnchantmentGlint;
    }
}