package stevekung.mods.indicatia.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import stevekung.mods.indicatia.utils.ThreadCheckMojangStatus;

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