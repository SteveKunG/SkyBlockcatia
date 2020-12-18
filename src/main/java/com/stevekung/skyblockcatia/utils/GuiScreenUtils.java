package com.stevekung.skyblockcatia.utils;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.inventory.IInventory;

public class GuiScreenUtils
{
    private static final ImmutableList<String> CHATABLE = ImmutableList.of("You                  ", "Ender Chest", "Craft Item", "Anvil", "Experimentation Table", "Reforge Item", "Trades", "Shop Trading Options", "Runic Pedestal", "Your Bids", "Bank", "Bank Deposit", "Bank Withdrawal", "Reforge Accessory Bag", "Catacombs Gate");
    public static final ImmutableList<String> INVENTORY = ImmutableList.of("Trades", "Shop Trading Options", "Backpack", "Chest", "Builder's Wand", "Basket of Seeds", "NetherWart Pouch", "Trick or Treat Bag", "Sack", "Anvil", "Enchant Item");

    public static boolean contains(List<String> invList, IInventory lowerChestInventory)
    {
        return invList.stream().anyMatch(invName -> lowerChestInventory.getDisplayName().getUnformattedText().contains(invName));
    }

    public static boolean equals(List<String> invList, IInventory lowerChestInventory)
    {
        return invList.stream().anyMatch(invName -> lowerChestInventory.getDisplayName().getUnformattedText().equals(invName));
    }

    public static boolean isChatable(IInventory lowerChestInventory)
    {
        return SkyBlockcatiaConfig.enableChatInContainerScreen && contains(CHATABLE, lowerChestInventory);
    }

    public static boolean canViewSeller(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Auctions Browser") || name.equals("Your Bids") || name.equals("Auction View");
    }

    public static boolean isAuctionBrowser(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Auctions Browser") || name.startsWith("Auctions:") || isOtherAuction(lowerChestInventory);
    }

    public static boolean canRenderBids(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.equals("Manage Auctions") || name.equals("Your Bids") || isAuctionBrowser(lowerChestInventory);
    }

    public static boolean isOtherAuction(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.endsWith("'s Auctions");
    }

    public static boolean isOtherProfile(IInventory lowerChestInventory)
    {
        String name = lowerChestInventory.getDisplayName().getUnformattedText();
        return name.endsWith("'s Profile") || name.endsWith("' Profile");
    }
}