package com.stevekung.skyblockcatia.core;

import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockcatiaCommand;
import com.stevekung.skyblockcatia.config.ConfigHandlerSB;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public class SkyBlockcatiaFabricMod implements ClientModInitializer
{
    public static final ConfigHandlerSB CONFIG = new ConfigHandlerSB();

    @Override
    public void onInitializeClient()
    {
        SkyBlockcatiaMod.init();

        new BazaarViewerCommand(ClientCommandManager.DISPATCHER);
        new SkyBlockAPIViewerCommand(ClientCommandManager.DISPATCHER);
        new SkyBlockcatiaCommand(ClientCommandManager.DISPATCHER);
    }
}