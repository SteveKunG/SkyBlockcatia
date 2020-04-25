package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.command.*;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

@Mixin(value = ClientCommandHandler.class, remap = false)
public abstract class ClientCommandHandlerMixin extends CommandHandler
{
    @Shadow(remap = false)
    protected abstract ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args);

    @Overwrite
    @Override
    public int executeCommand(ICommandSender sender, String message)
    {
        message = message.trim();

        if (!message.startsWith("/"))
        {
            return 0;
        }

        message = message.substring(1);
        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand icommand = this.getCommands().get(commandName);

        try
        {
            if (icommand == null)
            {
                return 0;
            }
            if (icommand.canCommandSenderUseCommand(sender))
            {
                CommandEvent event = new CommandEvent(icommand, sender, args);

                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    if (event.exception != null)
                    {
                        throw event.exception;
                    }
                    return 0;
                }
                icommand.processCommand(sender, args);
                return 1;
            }
            else
            {
                sender.addChatMessage(this.format(EnumChatFormatting.RED, "commands.generic.permission"));
            }
        }
        catch (WrongUsageException wue)
        {
            sender.addChatMessage(this.format(EnumChatFormatting.RED, "commands.generic.usage", this.format(EnumChatFormatting.RED, wue.getMessage(), wue.getErrorObjects())));
        }
        catch (CommandException ce)
        {
            sender.addChatMessage(this.format(EnumChatFormatting.RED, ce.getMessage(), ce.getErrorObjects()));
        }
        catch (Throwable t)
        {
            sender.addChatMessage(this.format(EnumChatFormatting.RED, "commands.generic.exception"));
            t.printStackTrace();
        }
        return -1;
    }
}