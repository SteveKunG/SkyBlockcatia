package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.PlatformConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "bobHurt", cancellable = true, at = @At("HEAD"))
    private void hurtCameraEffect(PoseStack stack, float partialTicks, CallbackInfo info)
    {
        if (PlatformConfig.getDisableCameraEffect())
        {
            info.cancel();
        }
    }

    @Inject(method = "getNightVisionScale", cancellable = true, at = @At("HEAD"))
    private static void getNightVisionBrightness(LivingEntity entity, float partialTicks, CallbackInfoReturnable<Float> info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableNightVision)
        {
            info.setReturnValue(0.0F);
        }
    }
}