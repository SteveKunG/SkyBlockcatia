package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;

@Mixin(EntityRendererManager.class)
public abstract class MixinEntityRendererManager
{
    @Inject(method = "renderDebugBoundingBox(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;F)V", cancellable = true, at = @At("HEAD"))
    private void renderDebugBoundingBox(MatrixStack matrixStack, IVertexBuilder buffer, Entity entity, float partialTicks, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.showDragonHitboxOnly && !(entity instanceof EnderDragonEntity || entity instanceof EnderCrystalEntity) && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            info.cancel();
        }
    }
}