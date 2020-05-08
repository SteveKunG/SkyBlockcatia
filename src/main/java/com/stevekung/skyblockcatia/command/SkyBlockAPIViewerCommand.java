package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSuggestionProvider;

public class SkyBlockAPIViewerCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("sbapi").then(ClientCommands.argument("target", StringArgumentType.greedyString()).executes(requirement -> SkyBlockAPIViewerCommand.setPlayerToView(StringArgumentType.getString(requirement, "target")))));
    }

    private static int setPlayerToView(String name)
    {
        MainEventHandler.playerToView = name;
        return 1;
    }
}