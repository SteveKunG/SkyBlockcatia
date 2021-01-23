package com.stevekung.skyblockcatia.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;

@Mixin(BucketItem.class)
public class MixinBucketItem
{
    @Redirect(method = "emptyBucket(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"))
    private ItemStack preventEmptyBucket(IItemProvider item, ItemStack itemStack, PlayerEntity player)
    {
        if (itemStack.hasTag())
        {
            CompoundNBT extra = itemStack.getTag().getCompound("ExtraAttributes");
            String id = extra.getString("id");

            if (id.equals("MAGICAL_WATER_BUCKET"))
            {
                return itemStack;
            }
        }
        return new ItemStack(item);
    }
}