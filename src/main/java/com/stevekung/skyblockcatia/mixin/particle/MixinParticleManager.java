package com.stevekung.skyblockcatia.mixin.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

@Mixin(ParticleManager.class)
public class MixinParticleManager
{
    @Inject(method = "addBlockDestroyEffects(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", cancellable = true, at = @At("HEAD"))
    private void addBlockDestroyEffects(BlockPos pos, BlockState state, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableBlockParticles)
        {
            info.cancel();
        }
    }

    @Inject(method = "addBlockHitEffects(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)V", cancellable = true, at = @At("HEAD"))
    private void addBlockHitEffects(BlockPos pos, Direction side, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableBlockParticles)
        {
            info.cancel();
        }
    }
}