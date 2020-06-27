package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;

import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(EntityRendererManager.class)
public abstract class MixinEntityRendererManager
{
    @Shadow
    private void renderBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float red, float green, float blue) {}

    @Inject(method = "renderDebugBoundingBox(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;Lnet/minecraft/entity/Entity;F)V", cancellable = true, at = @At("HEAD"))
    private void renderDebugBoundingBox(MatrixStack matrixStack, IVertexBuilder buffer, Entity entity, float partialTicks, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.showDragonHitboxOnly && !(entity instanceof EnderDragonEntity || entity instanceof EnderCrystalEntity) && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            info.cancel();
        }
    }

    @Overwrite
    private void renderDebugBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity entityIn, float partialTicks)
    {
        float f = entityIn.getWidth() / 2.0F;
        this.renderBoundingBox(matrixStackIn, bufferIn, entityIn, 1.0F, 1.0F, 1.0F);

        if (entityIn instanceof EnderDragonEntity)
        {
            double d0 = -MathHelper.lerp(partialTicks, entityIn.lastTickPosX, entityIn.getPosX());
            double d1 = -MathHelper.lerp(partialTicks, entityIn.lastTickPosY, entityIn.getPosY());
            double d2 = -MathHelper.lerp(partialTicks, entityIn.lastTickPosZ, entityIn.getPosZ());

            for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).getDragonParts())
            {
                matrixStackIn.push();
                double d3 = d0 + MathHelper.lerp(partialTicks, enderdragonpartentity.lastTickPosX, enderdragonpartentity.getPosX());
                double d4 = d1 + MathHelper.lerp(partialTicks, enderdragonpartentity.lastTickPosY, enderdragonpartentity.getPosY());
                double d5 = d2 + MathHelper.lerp(partialTicks, enderdragonpartentity.lastTickPosZ, enderdragonpartentity.getPosZ());
                matrixStackIn.translate(d3, d4, d5);
                this.renderBoundingBox(matrixStackIn, bufferIn, enderdragonpartentity, 0.25F, 1.0F, 0.0F);
                matrixStackIn.pop();
            }
        }

        if (entityIn instanceof LivingEntity)
        {
            WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, -f, entityIn.getEyeHeight() - 0.01F, -f, f, entityIn.getEyeHeight() + 0.01F, f, 1.0F, 0.0F, 0.0F, 1.0F);
        }

        Vec3d vec3d = entityIn.getLook(partialTicks);
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        bufferIn.pos(matrix4f, 0.0F, entityIn.getEyeHeight(), 0.0F).color(0, 0, 255, 255).endVertex();
        bufferIn.pos(matrix4f, (float)(vec3d.x * 2.0D), (float)(entityIn.getEyeHeight() + vec3d.y * 2.0D), (float)(vec3d.z * 2.0D)).color(0, 0, 255, 255).endVertex();
    }
}