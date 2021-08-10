package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.skyblockcatia.command.arguments.SkyblockBazaarItemIdArgumentType;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSharedSuggestionProvider;
import net.minecraft.ChatFormatting;

public class BazaarViewerCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSharedSuggestionProvider> dispatcher)
    {
        var node = dispatcher.register(ClientCommands.literal("sbbazaar").then(ClientCommands.argument("skyblock_item_id", SkyblockBazaarItemIdArgumentType.create()).executes(requirement -> BazaarViewerCommand.getBazaarData(SkyblockBazaarItemIdArgumentType.getItemId(requirement, "skyblock_item_id")))));
        dispatcher.register(ClientCommands.literal("sbcbazaar").redirect(node));
    }

    private static int getBazaarData(String itemId)
    {
        if (MainEventHandler.BAZAAR_DATA.isEmpty())
        {
            ClientUtils.printClientMessage(LangUtils.translate("commands.sbbazaar.empty_data", ChatFormatting.RED));
            return 1;
        }
        else if (MainEventHandler.BAZAAR_DATA.keySet().stream().anyMatch(product -> product.equals(itemId)))
        {
            var data = MainEventHandler.BAZAAR_DATA.get(itemId);
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Last Updated: " + ChatFormatting.WHITE + TimeUtils.getRelativeTime(data.lastUpdated()));
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Product: " + ChatFormatting.GOLD + itemId);
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Buy/Sell (Stack): " + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.status().buyPrice() * 64.0D) + "/" + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.status().sellPrice() * 64.0D));
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Buy/Sell (One): " + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.status().buyPrice()) + "/" + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.status().sellPrice()));
            return 1;
        }
        else
        {
            ClientUtils.printClientMessage(LangUtils.translate("commands.sbbazaar.id_not_found", ChatFormatting.RED));
            return 1;
        }
    }
}