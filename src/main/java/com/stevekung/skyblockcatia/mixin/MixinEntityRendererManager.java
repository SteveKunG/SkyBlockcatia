package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.HitboxRenderMode;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.EnderCrystalEntity;

@Mixin(EntityRendererManager.class)
public class MixinEntityRendererManager
{
    @Inject(method = "renderDebugBoundingBox(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;F)V", cancellable = true, at = @At("HEAD"))
    private void renderDebugBoundingBox(MatrixStack matrixStack, IVertexBuilder buffer, Entity entity, float partialTicks, CallbackInfo info)
    {
        HitboxRenderMode mode = SkyBlockcatiaSettings.INSTANCE.hitboxRenderMode;

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            if (mode == HitboxRenderMode.DRAGON && !(entity instanceof EnderDragonEntity) || mode == HitboxRenderMode.CRYSTAL && !(entity instanceof EnderCrystalEntity) || mode == HitboxRenderMode.DRAGON_AND_CRYSTAL && !(entity instanceof EnderDragonEntity || entity instanceof EnderCrystalEntity))
            {
                info.cancel();
            }
        }
    }
}