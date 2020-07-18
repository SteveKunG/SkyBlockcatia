package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.particle.EntityFirework;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityFirework.OverlayFX.class)
public abstract class EntityFirework_OverlayFXMixin
{
    @Inject(method = "renderParticle(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/WorldRenderer.pos(DDD)Lnet/minecraft/client/renderer/WorldRenderer;", ordinal = 0, shift = Shift.BEFORE))
    private void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_, CallbackInfo info)
    {
        GlStateManager.depthMask(false);
    }
}