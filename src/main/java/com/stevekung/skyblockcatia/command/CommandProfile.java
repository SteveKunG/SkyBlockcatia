package com.stevekung.skyblockcatia.command;

import java.io.File;
import java.util.*;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandProfile extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "inprofile";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.inprofile.usage");
        }
        else
        {
            if ("add".equalsIgnoreCase(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.inprofile.add.usage");
                }

                String name = args[1];
                boolean exist = false;

                if (name.equalsIgnoreCase("default"))
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.create_profile_default")).setChatStyle(JsonUtils.red()));
                    return;
                }

                for (File file : ExtendedConfig.userDir.listFiles())
                {
                    if (name.equalsIgnoreCase(file.getName().replace(".dat", "")))
                    {
                        exist = file.getName().equalsIgnoreCase(name + ".dat") && file.exists();
                    }
                }

                if (exist)
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.already_created", name)).setChatStyle(JsonUtils.red()));
                }
                else
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.profile_added", name)));
                    ExtendedConfig.instance.save(name);
                }
            }
            else if ("load".equalsIgnoreCase(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.inprofile.load.usage");
                }

                String name = args[1];

                for (File file : ExtendedConfig.userDir.listFiles())
                {
                    if (file.getName().contains(name) && file.getName().endsWith(".dat") && !file.exists())
                    {
                        sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.cant_load_profile")));
                        return;
                    }
                }

                ExtendedConfig.instance.setCurrentProfile(name);
                ExtendedConfig.saveProfileFile(name);
                ExtendedConfig.instance.load();
                sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.load_profile", name)));
                ExtendedConfig.instance.save(name); // save current settings
            }
            else if ("save".equalsIgnoreCase(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.inprofile.save.usage");
                }

                String name = args[1];
                boolean exist = false;

                for (File file : ExtendedConfig.userDir.listFiles())
                {
                    if (name.equalsIgnoreCase(file.getName().replace(".dat", "")))
                    {
                        exist = file.getName().equalsIgnoreCase(name + ".dat") && file.exists();
                    }
                }

                if (exist)
                {
                    ExtendedConfig.instance.save(name);
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.save_profile", name)));
                }
                else
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.cant_save_profile", name)).setChatStyle(JsonUtils.red()));
                }
            }
            else if ("remove".equalsIgnoreCase(args[0]))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.inprofile.remove.usage");
                }

                String name = args[1];

                if (name.equals("default"))
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.cannot_remove_default")).setChatStyle(JsonUtils.red()));
                    return;
                }

                boolean exist = false;

                for (File file : ExtendedConfig.userDir.listFiles())
                {
                    if (name.equalsIgnoreCase(file.getName().replace(".dat", "")))
                    {
                        exist = file.getName().equalsIgnoreCase(name + ".dat") && file.exists();
                    }
                }

                if (exist)
                {
                    File toDel = new File(ExtendedConfig.userDir, name + ".dat");
                    toDel.delete();
                    ExtendedConfig.instance.setCurrentProfile("default");
                    ExtendedConfig.instance.load();
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.remove_profile", name)));
                }
                else
                {
                    sender.addChatMessage(JsonUtils.create(LangUtils.translate("message.cant_remove_profile", name)).setChatStyle(JsonUtils.red()));
                }
            }
            else if ("list".equalsIgnoreCase(args[0]))
            {
                Collection<File> collection = new ArrayList<>(Arrays.asList(ExtendedConfig.userDir.listFiles()));

                if (collection.isEmpty())
                {
                    throw new CommandException("commands.inprofile.list.empty");
                }
                else
                {
                    int realSize = 0;

                    for (File file : collection)
                    {
                        if (file.getName().endsWith(".dat"))
                        {
                            ++realSize;
                        }
                    }

                    ChatComponentTranslation translation = new ChatComponentTranslation("commands.inprofile.list.count", realSize);
                    translation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
                    sender.addChatMessage(translation);

                    collection.forEach(file ->
                    {
                        String name = file.getName();
                        String realName = name.replace(".dat", "");
                        boolean current = realName.equals(ExtendedConfig.currentProfile);

                        if (name.endsWith(".dat"))
                        {
                            sender.addChatMessage(new ChatComponentTranslation("commands.inprofile.list.entry", realName, current ? "- " + EnumChatFormatting.RED + LangUtils.translate("commands.inprofile.current_profile") : ""));
                        }
                    });
                }
            }
            else
            {
                throw new WrongUsageException("commands.inprofile.usage");
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "load", "save", "remove", "list");
        }
        else if (args.length == 2)
        {
            if ("load".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0]) || "save".equalsIgnoreCase(args[0]))
            {
                if (ExtendedConfig.userDir.exists())
                {
                    List<String> list = new LinkedList<>();

                    for (File file : ExtendedConfig.userDir.listFiles())
                    {
                        String name = file.getName();

                        if (("load".equalsIgnoreCase(args[0]) || "save".equalsIgnoreCase(args[0]) || !name.equals("default.dat")) && name.endsWith(".dat"))
                        {
                            list.add(name.replace(".dat", ""));
                        }
                    }
                    return CommandBase.getListOfStringsMatchingLastWord(args, list);
                }
            }
        }
        return super.addTabCompletionOptions(sender, args, pos);
    }
}