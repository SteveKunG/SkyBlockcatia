package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.stevekung.skyblockcatia.gui.screen.config.SkyBlockSettingsScreen;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSharedSuggestionProvider;
import net.minecraft.client.Minecraft;

public class SkyBlockcatiaCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSharedSuggestionProvider> dispatcher)
    {
        LiteralCommandNode<IClientSharedSuggestionProvider> node = dispatcher.register(ClientCommands.literal("skyblockcatia").executes(requirement -> SkyBlockcatiaCommand.openSettingScreen()));
        dispatcher.register(ClientCommands.literal("sbc").redirect(node));
        dispatcher.register(ClientCommands.literal("sbcatia").redirect(node));
    }

    private static int openSettingScreen()
    {
        Minecraft.getInstance().setScreen(new SkyBlockSettingsScreen());
        return 1;
    }
}