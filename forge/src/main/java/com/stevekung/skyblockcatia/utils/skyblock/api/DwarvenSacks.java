package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum DwarvenSacks
{
    MITHRIL_ORE(new ItemStack(Items.PRISMARINE_CRYSTALS), TextComponentUtils.component("Mithril Ore")),
    TITANIUM_ORE(ItemUtils.getSkullItemStack("3904f020-0d31-3ec6-a626-cb2f141737de", "a14c6e41a762d37863a9fff6888c738905b92cc6c3898892a38dfdfe2ac4bf"), TextComponentUtils.formatted("Titanium", ChatFormatting.BLUE)),
    TREASURITE(ItemUtils.getSkullItemStack("163f66cb-f787-34c8-8861-f37c0d3d8bb3", "be261c6b852dd38e0d51349b1b8132887cd1b69da04d8daf0121af79ad8dd92"), TextComponentUtils.formatted("Treasurite", ChatFormatting.DARK_PURPLE)),
    STARFALL(new ItemStack(Items.NETHER_STAR), TextComponentUtils.formatted("Starfall", ChatFormatting.BLUE));

    private final ItemStack baseItem;
    private final Component displayName;

    DwarvenSacks(ItemStack baseItem, Component displayName)
    {
        this.baseItem = baseItem;
        this.displayName = displayName;
    }

    public ItemStack getBaseItem()
    {
        return this.baseItem;
    }

    public Component getDisplayName()
    {
        return this.displayName;
    }
}