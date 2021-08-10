package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class SkyBlockAPIViewerCommand
{
    public SkyBlockAPIViewerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        var node = dispatcher.register(ClientCommandManager.literal("sbapi").executes(requirement -> SkyBlockAPIViewerCommand.setPlayerToView(GameProfileUtils.getUsername())).then(ClientCommandManager.argument("target", StringArgumentType.greedyString()).executes(requirement -> SkyBlockAPIViewerCommand.setPlayerToView(StringArgumentType.getString(requirement, "target")))));
        dispatcher.register(ClientCommandManager.literal("sbcapi").redirect(node));
    }

    private static int setPlayerToView(String name)
    {
        MainEventHandler.playerToView = name;
        return 1;
    }
}