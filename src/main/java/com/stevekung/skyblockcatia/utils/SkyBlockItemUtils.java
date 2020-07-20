package com.stevekung.skyblockcatia.utils;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkyBlockItemUtils
{
    private static final ImmutableList<String> BLACKLIST = ImmutableList.of("SNOW_BLASTER", "SNOW_CANNON");
    public static final ImmutableList<String> CLICKABLE = ImmutableList.of("WEIRD_TUBA");

    public static ItemStack getBlockedItem(ItemStack itemStack, EntityPlayer player)
    {
        if (HypixelEventHandler.isSkyBlock && itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (!BLACKLIST.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingItem();
            }
        }
        return itemStack;
    }
}