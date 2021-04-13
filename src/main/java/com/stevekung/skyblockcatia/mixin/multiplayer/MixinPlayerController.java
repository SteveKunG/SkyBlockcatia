package com.stevekung.skyblockcatia.mixin.multiplayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.stevekungslib.utils.CommonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.util.math.BlockPos;

@Mixin(PlayerController.class)
public class MixinPlayerController
{
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "net/minecraft/world/World.getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;", shift = Shift.BEFORE))
    private void addBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> info)
    {
        CommonUtils.post(new ClientBlockBreakEvent(this.mc.world, pos));
    }
}