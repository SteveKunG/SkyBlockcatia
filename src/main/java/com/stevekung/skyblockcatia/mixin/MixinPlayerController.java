package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
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
    @Mutable
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "net/minecraft/block/Block.onBlockHarvested(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void onPlayerDestroyBlock(BlockPos pos, CallbackInfoReturnable info)
    {
        CommonUtils.post(new ClientBlockBreakEvent(this.mc.world, pos));
    }
}