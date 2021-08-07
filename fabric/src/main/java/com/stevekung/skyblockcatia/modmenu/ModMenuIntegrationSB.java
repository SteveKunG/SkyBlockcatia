package com.stevekung.skyblockcatia.modmenu;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegrationSB implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> AutoConfig.getConfigScreen(SkyBlockcatiaConfig.class, parent).get();
    }
}