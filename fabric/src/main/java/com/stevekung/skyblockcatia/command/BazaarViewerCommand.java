package com.stevekung.skyblockcatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.stevekung.skyblockcatia.command.arguments.SkyblockBazaarItemIdArgumentType;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.Bazaar;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.ChatFormatting;

public class BazaarViewerCommand
{
    public BazaarViewerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(ClientCommandManager.literal("sbbazaar").then(ClientCommandManager.argument("skyblock_item_id", SkyblockBazaarItemIdArgumentType.create()).executes(requirement -> BazaarViewerCommand.getBazaarData(SkyblockBazaarItemIdArgumentType.getItemId(requirement, "skyblock_item_id")))));
        dispatcher.register(ClientCommandManager.literal("sbcbazaar").redirect(node));
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
            Bazaar.Data data = MainEventHandler.BAZAAR_DATA.get(itemId);
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Last Updated: " + ChatFormatting.WHITE + TimeUtils.getRelativeTime(data.getLastUpdated()));
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Product: " + ChatFormatting.GOLD + itemId);
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Buy/Sell (Stack): " + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.getStatus().getBuyPrice() * 64.0D) + "/" + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.getStatus().getSellPrice() * 64.0D));
            ClientUtils.printClientMessage(ChatFormatting.YELLOW + "Buy/Sell (One): " + ChatFormatting.GOLD + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.getStatus().getBuyPrice()) + "/" + NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(data.getStatus().getSellPrice()));
            return 1;
        }
        else
        {
            ClientUtils.printClientMessage(LangUtils.translate("commands.sbbazaar.id_not_found", ChatFormatting.RED));
            return 1;
        }
    }
}