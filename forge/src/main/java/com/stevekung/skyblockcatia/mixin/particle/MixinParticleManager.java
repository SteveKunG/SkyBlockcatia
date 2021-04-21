package com.stevekung.skyblockcatia.mixin.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ParticleEngine.class)
public class MixinParticleManager
{
    @Inject(method = "destroy", cancellable = true, at = @At("HEAD"))
    private void addBlockDestroyEffects(BlockPos pos, BlockState state, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableBlockParticles)
        {
            info.cancel();
        }
    }

    @Inject(method = "crack", cancellable = true, at = @At("HEAD"))
    private void addBlockHitEffects(BlockPos pos, Direction side, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableBlockParticles)
        {
            info.cancel();
        }
    }
}