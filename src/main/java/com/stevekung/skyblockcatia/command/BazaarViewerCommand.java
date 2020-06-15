package com.stevekung.skyblockcatia.command;

import java.text.DecimalFormat;

import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.skyblockcatia.command.arguments.SkyblockBazaarItemIdArgumentType;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSuggestionProvider;

import net.minecraft.util.text.TextFormatting;

//TODO Fix unknown crash
public class BazaarViewerCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("sbbazaar").then(ClientCommands.argument("skyblock_item_id", SkyblockBazaarItemIdArgumentType.create()).executes(requirement -> BazaarViewerCommand.getBazaarData(SkyblockBazaarItemIdArgumentType.getItemId(requirement, "skyblock_item_id")))));
    }

    private static int getBazaarData(String itemId)
    {
        DecimalFormat format = new DecimalFormat("#,###.#");
        BazaarData data = MainEventHandler.BAZAAR_DATA.get(itemId);
        ClientUtils.printClientMessage(TextFormatting.YELLOW + "Last Updated: " + TextFormatting.WHITE + TimeUtils.getRelativeTime(data.getLastUpdated()));
        ClientUtils.printClientMessage(TextFormatting.YELLOW + "Product: " + TextFormatting.GOLD + itemId);
        ClientUtils.printClientMessage(TextFormatting.YELLOW + "Buy/Sell: " + TextFormatting.GOLD + format.format(data.getProduct().getBuyPrice()) + "/" + format.format(data.getProduct().getSellPrice()));
        return 1;
    }
}