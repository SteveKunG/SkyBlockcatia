package com.stevekung.skyblockcatia.utils;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import net.minecraft.network.chat.Component;

public class GuiScreenUtils
{
    private static final ImmutableList<String> CHATABLE = ImmutableList.of("You                  ", "Ender Chest", "Craft Item", "Anvil", "Experimentation Table", "Reforge Item", "Trades", "Shop Trading Options", "Runic Pedestal", "Your Bids", "Bank", "Bank Deposit", "Bank Withdrawal", "Reforge Accessory Bag", "Catacombs Gate");
    public static final ImmutableList<String> INVENTORY = ImmutableList.of("Trades", "Shop Trading Options", "Backpack", "Chest", "Builder's Wand", "Basket of Seeds", "NetherWart Pouch", "Trick or Treat Bag", "Sack", "Anvil", "Enchant Item");

    public static boolean contains(List<String> invList, Component title)
    {
        return invList.stream().anyMatch(title.getString()::contains);
    }

    public static boolean equals(List<String> invList, Component title)
    {
        return invList.stream().anyMatch(title.getString()::equals);
    }

    public static boolean isChatable(Component title)
    {
        return SkyBlockcatiaConfig.GENERAL.enableChatInContainerScreen.get() && contains(CHATABLE, title);
    }

    public static boolean canViewSeller(String title)
    {
        return title.equals("Auctions Browser") || title.equals("Your Bids") || title.equals("Auction View");
    }

    public static boolean isAuctionBrowser(String title)
    {
        return title.equals("Auctions Browser") || title.startsWith("Auctions:") || isOtherAuction(title);
    }

    public static boolean canRenderBids(String title)
    {
        return title.equals("Manage Auctions") || title.equals("Your Bids") || isAuctionBrowser(title);
    }

    public static boolean isOtherAuction(String title)
    {
        return title.endsWith("'s Auctions");
    }

    public static boolean isOtherProfile(String title)
    {
        return title.endsWith("'s Profile") || title.endsWith("' Profile") || title.endsWith("[GUEST]");
    }
}