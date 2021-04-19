package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public enum DwarvenSacks
{
    MITHRIL_ORE(new ItemStack(Items.prismarine_crystals), EnumChatFormatting.RESET + "Mithril Ore"),
    TITANIUM_ORE(RenderUtils.getSkullItemStack("3904f020-0d31-3ec6-a626-cb2f141737de", "a14c6e41a762d37863a9fff6888c738905b92cc6c3898892a38dfdfe2ac4bf"), EnumChatFormatting.BLUE + "Titanium"),
    TREASURITE(RenderUtils.getSkullItemStack("163f66cb-f787-34c8-8861-f37c0d3d8bb3", "be261c6b852dd38e0d51349b1b8132887cd1b69da04d8daf0121af79ad8dd92"), EnumChatFormatting.DARK_PURPLE + "Treasurite"),
    STARFALL(new ItemStack(Items.nether_star), EnumChatFormatting.BLUE + "Starfall");

    private final ItemStack baseItem;
    private final String displayName;

    DwarvenSacks(ItemStack baseItem, String displayName)
    {
        this.baseItem = baseItem;
        this.displayName = displayName;
    }

    public ItemStack getBaseItem()
    {
        return this.baseItem;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }
}