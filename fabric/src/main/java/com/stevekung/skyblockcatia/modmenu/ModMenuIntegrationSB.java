package com.stevekung.skyblockcatia.modmenu;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegrationSB implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return SkyBlockcatiaConfig::createConfigScreen;
    }
}