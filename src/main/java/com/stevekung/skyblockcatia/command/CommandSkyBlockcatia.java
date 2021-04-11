package com.stevekung.skyblockcatia.command;

import java.util.Arrays;
import java.util.List;

import com.stevekung.skyblockcatia.gui.config.GuiSkyBlockSettings;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandSkyBlockcatia extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "skyblockcatia";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("sbc", "sbcatia");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        GuiSkyBlockSettings options = new GuiSkyBlockSettings();
        options.display();
    }
}