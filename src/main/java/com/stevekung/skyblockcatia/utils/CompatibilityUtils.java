package com.stevekung.skyblockcatia.utils;

import java.lang.reflect.Field;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;

public class CompatibilityUtils
{
    public static final CompatibilityUtils INSTANCE = new CompatibilityUtils();

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherInventoryPosition;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;
    private boolean vanillaEnFixInventory;

    // Not Enough Updates Compatibility
    private static Class<?> neuConfig;
    private boolean neuhidePotionEffect;

    public CompatibilityUtils()
    {
        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
                Field inventoryPosition = patcherConfig.getDeclaredField("inventoryPosition");
                this.patcherInventoryPosition = inventoryPosition.getBoolean(patcherConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isVanillaEnhancementsLoaded)
        {
            try
            {
                vanillaEnConfig = Class.forName("com.orangemarshall.enhancements.config.Config");
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                Field fixInventory = instance.getClass().getDeclaredField("fixInventory");
                this.vanillaEnFixInventory = fixInventory.getBoolean(instance);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isNotEnoughUpdatesLoaded)
        {
            try
            {
                neuConfig = Class.forName("io.github.moulberry.notenoughupdates.NotEnoughUpdates");
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
    }

    public boolean hasInventoryFix()
    {
        return this.vanillaEnFixInventory || this.patcherInventoryPosition || this.neuhidePotionEffect;
    }
}