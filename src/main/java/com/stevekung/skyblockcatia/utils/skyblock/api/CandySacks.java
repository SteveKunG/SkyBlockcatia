package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public enum CandySacks
{
    GREEN_CANDY(ItemUtils.getSkullItemStack("0961dbb3-2167-3f75-92e4-ec8eb4f57e55", "ce0622d01cfdae386cc7dd83427674b422f46d0a57e67a20607e6ca4b9af3b01"), TextComponentUtils.formatted("Green Candy", TextFormatting.GREEN)),
    PURPLE_CANDY(ItemUtils.getSkullItemStack("5b0e6bf0-6312-3476-b5f8-dbc9a8849a1f", "95d7aee4e97ad84095f55405ee1305d1fc8554c309edb12a1db863cde9c1ec80"), TextComponentUtils.formatted("Purple Candy", TextFormatting.DARK_PURPLE));

    private final ItemStack baseItem;
    private final ITextComponent displayName;

    private CandySacks(ItemStack baseItem, ITextComponent displayName)
    {
        this.baseItem = baseItem;
        this.displayName = displayName;
    }

    public ItemStack getBaseItem()
    {
        return this.baseItem;
    }

    public ITextComponent getDisplayName()
    {
        return this.displayName;
    }
}