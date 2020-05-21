package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBItemUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeItem;

@Mixin(Item.class)
public abstract class MixinItem implements IForgeItem
{
    @Inject(method = "onItemRightClick(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true, at = @At("HEAD"))
    private void onItemRightClick(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> info)
    {
        ItemStack itemStack = player.getHeldItem(hand);

        if (SkyBlockEventHandler.isSkyBlock && !itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");

            if (SBItemUtils.CLICKABLE.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                player.swingArm(Hand.MAIN_HAND);
                info.setReturnValue(ActionResult.resultPass(player.getHeldItem(hand)));
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (SkyBlockEventHandler.isSkyBlock && !newStack.isEmpty() && !oldStack.isEmpty() && (oldStack.getItem() == Items.BOW || oldStack.getItem() == Items.IRON_AXE) && oldStack.getItem() == newStack.getItem())
        {
            return false;
        }
        return !oldStack.equals(newStack);
    }
}