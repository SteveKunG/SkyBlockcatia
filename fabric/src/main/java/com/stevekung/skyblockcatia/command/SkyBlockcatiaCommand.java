package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.skyblockcatia.gui.screen.config.SkyBlockSettingsScreen;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class SkyBlockcatiaCommand
{
    public SkyBlockcatiaCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        var node = dispatcher.register(ClientCommandManager.literal("skyblockcatia").executes(requirement -> SkyBlockcatiaCommand.openSettingScreen()));
        dispatcher.register(ClientCommandManager.literal("sbc").redirect(node));
        dispatcher.register(ClientCommandManager.literal("sbcatia").redirect(node));
    }

    private static int openSettingScreen()
    {
        Minecraft.getInstance().setScreen(new SkyBlockSettingsScreen());
        return 1;
    }
}