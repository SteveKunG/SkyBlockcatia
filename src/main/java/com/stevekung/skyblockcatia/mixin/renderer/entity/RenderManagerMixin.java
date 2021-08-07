package com.stevekung.skyblockcatia.mixin.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.HitboxRenderMode;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.AxisAlignedBB;

@Mixin(RenderManager.class)
public class RenderManagerMixin
{
    @Shadow
    double renderPosX;

    @Shadow
    double renderPosY;

    @Shadow
    double renderPosZ;

    @Inject(method = "renderDebugBoundingBox(Lnet/minecraft/entity/Entity;DDDFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/RenderGlobal.drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;IIII)V", ordinal = 0))
    private void renderDebugBoundingBoxMultipart(Entity entity, double x, double y, double z, float f1, float partialTicks, CallbackInfo info)
    {
        Entity[] aentity = entity.getParts();

        if (aentity != null)
        {
            for (Entity partEntity : aentity)
            {
                double d0 = (partEntity.posX -partEntity.prevPosX) * partialTicks;
                double d1 = (partEntity.posY - partEntity.prevPosY) * partialTicks;
                double d2 = (partEntity.posZ - partEntity.prevPosZ) * partialTicks;
                AxisAlignedBB axisalignedbb1 = partEntity.getEntityBoundingBox();
                RenderManagerMixin.drawBoundingBox(axisalignedbb1.minX - this.renderPosX + d0, axisalignedbb1.minY - this.renderPosY + d1, axisalignedbb1.minZ - this.renderPosZ + d2, axisalignedbb1.maxX - this.renderPosX + d0, axisalignedbb1.maxY - this.renderPosY + d1, axisalignedbb1.maxZ - this.renderPosZ + d2, 0.25F, 1.0F, 0.0F, 1.0F);
            }
        }
    }

    @Inject(method = "renderDebugBoundingBox(Lnet/minecraft/entity/Entity;DDDFF)V", cancellable = true, at = @At("HEAD"))
    private void renderDebugBoundingBox(Entity entity, double x, double y, double z, float f1, float partialTicks, CallbackInfo info)
    {
        HitboxRenderMode mode = HitboxRenderMode.byId(SkyBlockcatiaSettings.INSTANCE.hitboxRenderMode);

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            if (mode == HitboxRenderMode.DRAGON && !(entity instanceof EntityDragon) || mode == HitboxRenderMode.CRYSTAL && !(entity instanceof EntityEnderCrystal) || mode == HitboxRenderMode.DRAGON_AND_CRYSTAL && !(entity instanceof EntityDragon || entity instanceof EntityEnderCrystal))
            {
                info.cancel();
            }
        }
    }

    private static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(worldRenderer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    private static void drawBoundingBox(WorldRenderer worldRenderer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        worldRenderer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
        worldRenderer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        worldRenderer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        worldRenderer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
    }
}