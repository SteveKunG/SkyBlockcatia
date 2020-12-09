package com.stevekung.skyblockcatia.command;

import java.util.Arrays;
import java.util.List;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.config.GuiExtendedConfig;
import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;

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
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.skyblockcatia.usage");
        }
        else
        {
            if ("toggle_sprint".equalsIgnoreCase(args[0]))
            {
                if (args.length == 1)
                {
                    throw new WrongUsageException("commands.skyblockcatia.togglesprint.usage");
                }

                if ("enable".equalsIgnoreCase(args[1]))
                {
                    SkyBlockcatiaSettings.INSTANCE.toggleSprint = true;
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sprint_enabled")));
                    SkyBlockcatiaSettings.INSTANCE.save();
                }
                else if ("disable".equalsIgnoreCase(args[1]))
                {
                    SkyBlockcatiaSettings.INSTANCE.toggleSprint = false;
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sprint_disabled")));
                    SkyBlockcatiaSettings.INSTANCE.save();
                }
                else if ("mode".equalsIgnoreCase(args[1]))
                {
                    if (args.length < 3 || args.length > 3)
                    {
                        throw new WrongUsageException("commands.skyblockcatia.togglesprint.mode.usage");
                    }

                    if ("key_binding".equalsIgnoreCase(args[2]))
                    {
                        SkyBlockcatiaSettings.INSTANCE.toggleSprintUseMode = "key_binding";
                        sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sprint_set") + LangUtils.translate("message.key_binding")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }
                    else if ("command".equalsIgnoreCase(args[2]))
                    {
                        SkyBlockcatiaSettings.INSTANCE.toggleSprintUseMode = "command";
                        sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sprint_set") + LangUtils.translate("message.command")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }
                    else
                    {
                        throw new WrongUsageException("commands.skyblockcatia.togglesprint.mode.usage");
                    }
                }
                else
                {
                    throw new WrongUsageException("commands.skyblockcatia.togglesprint.usage");
                }
            }
            else if ("toggle_sneak".equalsIgnoreCase(args[0]))
            {
                if (args.length == 1)
                {
                    throw new WrongUsageException("commands.skyblockcatia.togglesneak.usage");
                }

                if ("enable".equalsIgnoreCase(args[1]))
                {
                    SkyBlockcatiaSettings.INSTANCE.toggleSneak = true;
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sneak_enabled")));
                    SkyBlockcatiaSettings.INSTANCE.save();
                }
                else if ("disable".equalsIgnoreCase(args[1]))
                {
                    SkyBlockcatiaSettings.INSTANCE.toggleSneak = false;
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sneak_disabled")));
                    SkyBlockcatiaSettings.INSTANCE.save();
                }
                else if ("mode".equalsIgnoreCase(args[1]))
                {
                    if (args.length < 3 || args.length > 3)
                    {
                        throw new WrongUsageException(LangUtils.translate("message.toggle_sneak_set") + LangUtils.translate("message.key_binding"));
                    }
                    if ("key_binding".equalsIgnoreCase(args[2]))
                    {
                        SkyBlockcatiaSettings.INSTANCE.toggleSneakUseMode = "key_binding";
                        sender.addChatMessage(JsonUtils.create("Set toggle sneak to use Key Binding"));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }
                    else if ("command".equalsIgnoreCase(args[2]))
                    {
                        SkyBlockcatiaSettings.INSTANCE.toggleSneakUseMode = "command";
                        sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.toggle_sneak_set") + LangUtils.translate("message.command")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }
                    else
                    {
                        throw new WrongUsageException("commands.skyblockcatia.togglesneak.mode.usage");
                    }
                }
                else
                {
                    throw new WrongUsageException("commands.skyblockcatia.togglesneak.usage");
                }
            }
            else if ("gui".equalsIgnoreCase(args[0]))
            {
                GuiExtendedConfig options = new GuiExtendedConfig();
                options.display();
            }
            else
            {
                throw new WrongUsageException("commands.skyblockcatia.usage");
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, "toggle_sprint", "toggle_sneak", "gui");
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("toggle_sprint") || args[0].equalsIgnoreCase("toggle_sneak"))
            {
                return CommandBase.getListOfStringsMatchingLastWord(args, "enable", "disable", "mode");
            }
        }
        else if (args.length == 3)
        {
            if ((args[0].equalsIgnoreCase("toggle_sprint") || args[0].equalsIgnoreCase("toggle_sneak")) && args[1].equalsIgnoreCase("mode"))
            {
                return CommandBase.getListOfStringsMatchingLastWord(args, "key_binding", "command");
            }
        }
        return super.addTabCompletionOptions(sender, args, pos);
    }
}