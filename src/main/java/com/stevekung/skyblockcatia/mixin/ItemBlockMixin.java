package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.SkyBlockItemUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

@Mixin(ItemBlock.class)
public abstract class ItemBlockMixin extends Item
{
    @Inject(method = "onItemUse(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;FFF)Z", cancellable = true, at = @At("HEAD"))
    private void onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> info)
    {
        if (HypixelEventHandler.isSkyBlock && itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound extraAttrib = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");

            if (SkyBlockItemUtils.CLICKABLE.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingItem();
                info.setReturnValue(false);
            }
        }
    }
}