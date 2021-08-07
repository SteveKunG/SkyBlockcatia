package com.stevekung.skyblockcatia.gui.screen.config.fabric;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class SkyBlockSettingsScreenImpl
{
    public static void openConfig(Screen parent)
    {
        Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(SkyBlockcatiaConfig.class, parent).get());
    }
}