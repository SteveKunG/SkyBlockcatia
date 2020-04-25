package com.stevekung.skyblockcatia.config;

import java.util.Set;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft mc) {}

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return GuiMainConfig.class;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }

    public static class GuiMainConfig extends GuiConfig
    {
        public GuiMainConfig(GuiScreen gui)
        {
            super(gui, ConfigManagerIN.getConfigElements(), SkyBlockcatiaMod.MOD_ID, false, false, LangUtils.translate("gui.config.skyblockcatia.name"));
        }
    }
}