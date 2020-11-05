package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ExtendedConfig;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@Mixin(EffectRenderer.class)
public class EffectRendererMixin
{
    @Inject(method = "addBlockDestroyEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", cancellable = true, at = @At("HEAD"))
    private void addBlockDestroyEffects(BlockPos pos, IBlockState state, CallbackInfo info)
    {
        if (ExtendedConfig.instance.disableBlockParticles)
        {
            info.cancel();
        }
    }

    @Inject(method = "addBlockHitEffects(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)V", cancellable = true, at = @At("HEAD"))
    private void addBlockHitEffects(BlockPos pos, EnumFacing side, CallbackInfo info)
    {
        if (ExtendedConfig.instance.disableBlockParticles)
        {
            info.cancel();
        }
    }
}