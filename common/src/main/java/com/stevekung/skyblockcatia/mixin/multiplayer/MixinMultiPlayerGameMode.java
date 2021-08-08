package com.stevekung.skyblockcatia.mixin.multiplayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;

@Mixin(MultiPlayerGameMode.class)
public class MixinMultiPlayerGameMode
{
    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", shift = Shift.BEFORE))
    private void addBlockBreakEvent(BlockPos pos, CallbackInfoReturnable<Boolean> info)
    {
        ClientBlockBreakEvent.CLIENT_BLOCK_BREAK.invoker().onBlockBreak(this.minecraft.level, pos, null);
    }
}