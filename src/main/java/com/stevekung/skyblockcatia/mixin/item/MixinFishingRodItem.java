package com.stevekung.skyblockcatia.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import com.stevekung.stevekungslib.utils.CommonUtils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(FishingRodItem.class)
public class MixinFishingRodItem
{
    @Inject(method = "onItemRightClick(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "net/minecraft/world/World.playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V", ordinal = 0, shift = Shift.BEFORE))
    private void onItemRightClick(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ItemStack> info)
    {
        CommonUtils.post(new GrapplingHookEvent(player.getHeldItem(hand)));
    }
}