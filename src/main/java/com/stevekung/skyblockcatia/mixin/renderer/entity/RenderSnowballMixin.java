package com.stevekung.skyblockcatia.mixin.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSnowball;

@Mixin(RenderSnowball.class)
public class RenderSnowballMixin
{
    @Redirect(method = "doRender(Lnet/minecraft/entity/Entity;DDDFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.rotate(FFFF)V", ordinal = 1))
    private void rotate(float angle, float x, float y, float z)
    {
        GlStateManager.rotate((((RenderSnowball) (Object) this).getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * ((RenderSnowball) (Object) this).getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
    }
}