package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Inject(method = "hurtCameraEffect(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", cancellable = true, at = @At("HEAD"))
    private void hurtCameraEffect(MatrixStack stack, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.GENERAL.disableHurtCameraEffect.get())
        {
            info.cancel();
        }
    }
}