package com.stevekung.skyblockcatia.gui.screen.config.forge;

import java.io.File;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import net.minecraft.Util;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

public class SkyBlockSettingsScreenImpl
{
    public static void openConfig()
    {
        String configPath = ConfigTracker.INSTANCE.getConfigFileName(SkyBlockcatiaMod.MOD_ID, ModConfig.Type.CLIENT);

        if (configPath == null)
        {
            return;
        }
        File config = new File(configPath);
        Util.getPlatform().openUri(config.toURI());
    }
}