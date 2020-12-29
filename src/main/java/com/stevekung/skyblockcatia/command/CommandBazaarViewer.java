package com.stevekung.skyblockcatia.command;

import java.util.Arrays;
import java.util.List;

import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.ClientUtils;
import com.stevekung.skyblockcatia.utils.CommonUtils;
import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.ModDecimalFormat;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public class CommandBazaarViewer extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "sbbazaar";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("sbcbazaar");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("/sbbazaar <skyblock_item_id>");
        }
        else
        {
            if (MainEventHandler.BAZAAR_DATA.keySet().stream().anyMatch(product -> product.equals(args[0])))
            {
                ModDecimalFormat format = new ModDecimalFormat("#,###.#");
                BazaarData data = MainEventHandler.BAZAAR_DATA.get(args[0]);
                ClientUtils.printClientMessage(EnumChatFormatting.YELLOW + "Last Updated: " + EnumChatFormatting.WHITE + CommonUtils.getRelativeTime(data.getLastUpdated()));
                ClientUtils.printClientMessage(EnumChatFormatting.YELLOW + "Product: " + EnumChatFormatting.GOLD + args[0]);
                ClientUtils.printClientMessage(EnumChatFormatting.YELLOW + "Buy/Sell (Stack): " + EnumChatFormatting.GOLD + format.format(data.getProduct().getBuyPrice() * 64.0D) + "/" + format.format(data.getProduct().getSellPrice() * 64.0D));
                ClientUtils.printClientMessage(EnumChatFormatting.YELLOW + "Buy/Sell (One): " + EnumChatFormatting.GOLD + format.format(data.getProduct().getBuyPrice()) + "/" + format.format(data.getProduct().getSellPrice()));
            }
            else
            {
                ClientUtils.printClientMessage("Couldn't find matched Skyblock Item ID!", JsonUtils.red());
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, MainEventHandler.BAZAAR_DATA.keySet());
        }
        return super.addTabCompletionOptions(sender, args, pos);
    }
}