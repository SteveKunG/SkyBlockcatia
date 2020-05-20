package com.stevekung.skyblockcatia.utils;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SkyBlockItemUtils
{
    private static final ImmutableList<String> BLACKLIST = ImmutableList.of("SNOW_BLASTER");
    private static final ImmutableList<String> CLICKABLE = ImmutableList.of("WEIRD_TUBA");

    public static ItemStack getBlockedItem(ItemStack itemStack, EntityPlayer player)
    {
        if (HypixelEventHandler.isSkyBlock && itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (BLACKLIST.stream().anyMatch(id -> !extraAttrib.getString("id").equals(id)))
            {
                player.swingItem();
            }
        }
        return itemStack;
    }

    public static void getClickableItem(ItemStack itemStack, EntityPlayer player, CallbackInfoReturnable info)
    {
        if (itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (CLICKABLE.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingItem();

                if (info != null)
                {
                    info.setReturnValue(false);
                }
            }
        }
    }
}
