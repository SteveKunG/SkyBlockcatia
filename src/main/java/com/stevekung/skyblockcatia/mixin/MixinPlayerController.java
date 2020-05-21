package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.stevekungslib.utils.CommonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;

@Mixin(PlayerController.class)
public class MixinPlayerController
{
    @Shadow
    @Final
    @Mutable
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "net/minecraft/world/World.getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/IFluidState;", shift = Shift.BEFORE))
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> info)
    {
        CommonUtils.post(new ClientBlockBreakEvent(this.mc.world, pos));
    }

    @Inject(method = "func_217292_a(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/client/entity/player/ClientPlayerEntity.getHeldItem(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = Shift.AFTER))
    private void onRightClick(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> info)
    {
        ItemStack itemStack = player.getHeldItem(hand);

        if (SkyBlockEventHandler.isSkyBlock && !itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT extraAttrib = itemStack.getTag().getCompound("ExtraAttributes");

            if (extraAttrib.getString("id").equals("SNOW_BLASTER") || extraAttrib.getString("id").equals("WEIRD_TUBA"))
            {
                info.setReturnValue(ActionResultType.PASS);
            }
        }
    }
}