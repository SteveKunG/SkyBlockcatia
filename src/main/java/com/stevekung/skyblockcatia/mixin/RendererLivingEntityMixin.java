package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.utils.EntityOtherFakePlayer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin extends Render<EntityLivingBase>
{
    private RendererLivingEntityMixin()
    {
        super(null);
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", cancellable = true, at = @At("HEAD"))
    private void renderName(EntityLivingBase entity, CallbackInfoReturnable info)
    {
        if (entity instanceof EntityOtherFakePlayer)
        {
            info.setReturnValue(false);
        }
    }

    @Redirect(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.rotate(FFFF)V", ordinal = 1))
    private void rotate(float angle, float x, float y, float z)
    {
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}