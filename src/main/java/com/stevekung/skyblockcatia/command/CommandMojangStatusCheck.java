package com.stevekung.skyblockcatia.command;

import com.stevekung.skyblockcatia.utils.ThreadCheckMojangStatus;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandMojangStatusCheck extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "mojangstatus";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        ThreadCheckMojangStatus thread = new ThreadCheckMojangStatus();
        thread.start();
    }
}