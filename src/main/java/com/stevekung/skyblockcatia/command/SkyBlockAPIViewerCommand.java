package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSuggestionProvider;

public class SkyBlockAPIViewerCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSuggestionProvider> dispatcher)
    {
        LiteralCommandNode<IClientSuggestionProvider> node = dispatcher.register(ClientCommands.literal("sbapi").executes(requirement -> SkyBlockAPIViewerCommand.setPlayerToView(GameProfileUtils.getUsername())).then(ClientCommands.argument("target", StringArgumentType.greedyString()).executes(requirement -> SkyBlockAPIViewerCommand.setPlayerToView(StringArgumentType.getString(requirement, "target")))));
        dispatcher.register(ClientCommands.literal("sbcapi").redirect(node));
    }

    private static int setPlayerToView(String name)
    {
        MainEventHandler.playerToView = name;
        return 1;
    }
}