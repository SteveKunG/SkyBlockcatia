package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.SkyBlockItemUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Mixin(Item.class)
public abstract class ItemMixin
{
    @Inject(method = "shouldCauseReequipAnimation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z", cancellable = true, at = @At("HEAD"), remap = false)
    private void disableReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged, CallbackInfoReturnable info)
    {
        if (HypixelEventHandler.isSkyBlock && newStack != null && oldStack != null && (oldStack.getItem() == Items.bow || oldStack.getItem() == Items.iron_axe) && oldStack.getItem() == newStack.getItem())
        {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", cancellable = true, at = @At("HEAD"))
    private void onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, CallbackInfoReturnable<ItemStack> info)
    {
        if (HypixelEventHandler.isSkyBlock && itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (SkyBlockItemUtils.CLICKABLE.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingItem();
            }
        }
    }
}