package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @Shadow
    Minecraft mc;

    @Inject(method = "hurtCameraEffect(F)V", cancellable = true, at = @At("HEAD"))
    private void hurtCameraEffect(float partialTicks, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.disableHurtCameraEffect)
        {
            info.cancel();
        }
    }

    @Inject(method = "getNightVisionBrightness(Lnet/minecraft/entity/EntityLivingBase;F)F", cancellable = true, at = @At("HEAD"))
    private void getNightVisionBrightness(EntityLivingBase living, float partialTicks, CallbackInfoReturnable<Float> info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableNightVision)
        {
            info.setReturnValue(0.0F);
        }
    }
}