package com.stevekung.skyblockcatia.gui.screen.config.forge;

import java.io.File;

import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class SkyBlockSettingsScreenImpl
{
    public static void openConfig(Screen parent)
    {
        var configPath = ConfigTracker.INSTANCE.getConfigFileName(SkyBlockcatia.MOD_ID, ModConfig.Type.CLIENT);

        if (configPath == null)
        {
            return;
        }
        var config = new File(configPath);
        Util.getPlatform().openUri(config.toURI());
    }
}