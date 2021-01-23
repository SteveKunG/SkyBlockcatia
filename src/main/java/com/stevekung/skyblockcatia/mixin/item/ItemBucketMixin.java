package com.stevekung.skyblockcatia.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Mixin(ItemBucket.class)
public class ItemBucketMixin
{
    @Redirect(method = "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"))
    private ItemStack preventEmptyBucket(Item item, ItemStack itemStack, World world, EntityPlayer player)
    {
        if (itemStack.hasTagCompound())
        {
            NBTTagCompound extra = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            String id = extra.getString("id");

            if (id.equals("MAGICAL_WATER_BUCKET"))
            {
                return itemStack;
            }
        }
        return new ItemStack(item);
    }
}