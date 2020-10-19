package com.stevekung.skyblockcatia.utils.skyblock;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class SBItemUtils
{
    public static final ImmutableList<String> BLACKLIST = ImmutableList.of("SNOW_BLASTER", "SNOW_CANNON");
    public static final ImmutableList<String> CLICKABLE = ImmutableList.of("WEIRD_TUBA", "BAT_WAND");

    public static ActionResult<ItemStack> getBlockedItem(ItemStack itemStack, PlayerEntity player, ActionResult<ItemStack> defaultValue)
    {
        if (SkyBlockEventHandler.isSkyBlock && !itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");

            if (!BLACKLIST.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingArm(Hand.MAIN_HAND);
            }
        }
        return defaultValue;
    }

    public static ActionResultType getBlockedItemResult(ItemStack itemStack, PlayerEntity player, ActionResultType defaultValue)
    {
        if (SkyBlockEventHandler.isSkyBlock && !itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");

            if (!BLACKLIST.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingArm(Hand.MAIN_HAND);
                return ActionResultType.SUCCESS;
            }
        }
        return defaultValue;
    }
}