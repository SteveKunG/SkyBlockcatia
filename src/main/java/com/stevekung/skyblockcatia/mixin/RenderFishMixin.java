package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@Mixin(RenderFish.class)
public abstract class RenderFishMixin extends Render<Entity>
{
    private RenderFishMixin()
    {
        super(null);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        EntityFishHook fish = (EntityFishHook)entity;

        if (ConfigManagerIN.enableOldFishingRodRenderModel)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(fish);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            worldrenderer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();

            if (fish.angler != null)
            {
                float f7 = fish.angler.getSwingProgress(partialTicks);
                float f8 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
                Vec3 vec3 = new Vec3(-0.5D, 0.03D, 0.8D);
                vec3 = vec3.rotatePitch(-(fish.angler.prevRotationPitch + (fish.angler.rotationPitch - fish.angler.prevRotationPitch) * partialTicks) * (float)Math.PI / 180.0F);
                vec3 = vec3.rotateYaw(-(fish.angler.prevRotationYaw + (fish.angler.rotationYaw - fish.angler.prevRotationYaw) * partialTicks) * (float)Math.PI / 180.0F);
                vec3 = vec3.rotateYaw(f8 * 0.5F);
                vec3 = vec3.rotatePitch(-f8 * 0.7F);
                double d0 = fish.angler.prevPosX + (fish.angler.posX - fish.angler.prevPosX) * partialTicks + vec3.xCoord;
                double d1 = fish.angler.prevPosY + (fish.angler.posY - fish.angler.prevPosY) * partialTicks + vec3.yCoord;
                double d2 = fish.angler.prevPosZ + (fish.angler.posZ - fish.angler.prevPosZ) * partialTicks + vec3.zCoord;
                double d3 = fish.angler.getEyeHeight();
                double dz = 0.0D;

                if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || fish.angler != Minecraft.getMinecraft().thePlayer)
                {
                    double xz = fish.angler.isSneaking() ? 0.775D : 0.9D;
                    float f9 = (fish.angler.prevRenderYawOffset + (fish.angler.renderYawOffset - fish.angler.prevRenderYawOffset) * partialTicks) * (float)Math.PI / 180.0F;
                    double d4 = MathHelper.sin(f9);
                    double d6 = MathHelper.cos(f9);
                    d0 = fish.angler.prevPosX + (fish.angler.posX - fish.angler.prevPosX) * partialTicks - d6 * 0.35D - d4 * xz;
                    d1 = fish.angler.prevPosY + d3 + (fish.angler.posY - fish.angler.prevPosY) * partialTicks - 0.4D;
                    d2 = fish.angler.prevPosZ + (fish.angler.posZ - fish.angler.prevPosZ) * partialTicks - d4 * 0.35D + d6 * xz;
                    d3 = fish.angler.isSneaking() ? -0.45D : 0.0D;
                    dz = fish.angler.isSneaking() ? 0.015D : 0.0D;
                }

                double d13 = fish.prevPosX + (fish.posX - fish.prevPosX) * partialTicks;
                double d5 = fish.prevPosY + (fish.posY - fish.prevPosY) * partialTicks + 0.25D;
                double d7 = fish.prevPosZ + (fish.posZ - fish.prevPosZ) * partialTicks;
                double d9 = (float)(d0 - d13) + dz;
                double d11 = (float)(d1 - d5) + d3;
                double d12 = (float)(d2 - d7) + dz;
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

                for (int l = 0; l <= 16; ++l)
                {
                    float f10 = l / 16.0F;
                    worldrenderer.pos(x + d9 * f10, y + d11 * (f10 * f10 + f10) * 0.5D + 0.25D, z + d12 * f10).color(0, 0, 0, 255).endVertex();
                }
                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                super.doRender(fish, x, y, z, entityYaw, partialTicks);
            }
        }
        else
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(fish);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            worldrenderer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldrenderer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();

            if (fish.angler != null)
            {
                float f7 = fish.angler.getSwingProgress(partialTicks);
                float f8 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float)Math.PI);
                Vec3 vec3 = new Vec3(-0.36D, 0.03D, 0.35D);
                vec3 = vec3.rotatePitch(-(fish.angler.prevRotationPitch + (fish.angler.rotationPitch - fish.angler.prevRotationPitch) * partialTicks) * (float)Math.PI / 180.0F);
                vec3 = vec3.rotateYaw(-(fish.angler.prevRotationYaw + (fish.angler.rotationYaw - fish.angler.prevRotationYaw) * partialTicks) * (float)Math.PI / 180.0F);
                vec3 = vec3.rotateYaw(f8 * 0.5F);
                vec3 = vec3.rotatePitch(-f8 * 0.7F);
                double d0 = fish.angler.prevPosX + (fish.angler.posX - fish.angler.prevPosX) * partialTicks + vec3.xCoord;
                double d1 = fish.angler.prevPosY + (fish.angler.posY - fish.angler.prevPosY) * partialTicks + vec3.yCoord;
                double d2 = fish.angler.prevPosZ + (fish.angler.posZ - fish.angler.prevPosZ) * partialTicks + vec3.zCoord;
                double d3 = fish.angler.getEyeHeight();

                if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || fish.angler != Minecraft.getMinecraft().thePlayer)
                {
                    float f9 = (fish.angler.prevRenderYawOffset + (fish.angler.renderYawOffset - fish.angler.prevRenderYawOffset) * partialTicks) * (float)Math.PI / 180.0F;
                    double d4 = MathHelper.sin(f9);
                    double d6 = MathHelper.cos(f9);
                    d0 = fish.angler.prevPosX + (fish.angler.posX - fish.angler.prevPosX) * partialTicks - d6 * 0.35D - d4 * 0.8D;
                    d1 = fish.angler.prevPosY + d3 + (fish.angler.posY - fish.angler.prevPosY) * partialTicks - 0.45D;
                    d2 = fish.angler.prevPosZ + (fish.angler.posZ - fish.angler.prevPosZ) * partialTicks - d4 * 0.35D + d6 * 0.8D;
                    d3 = fish.angler.isSneaking() ? -0.1875D : 0.0D;
                }

                double d13 = fish.prevPosX + (fish.posX - fish.prevPosX) * partialTicks;
                double d5 = fish.prevPosY + (fish.posY - fish.prevPosY) * partialTicks + 0.25D;
                double d7 = fish.prevPosZ + (fish.posZ - fish.prevPosZ) * partialTicks;
                double d9 = (float)(d0 - d13);
                double d11 = (float)(d1 - d5) + d3;
                double d12 = (float)(d2 - d7);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

                for (int l = 0; l <= 16; ++l)
                {
                    float f10 = l / 16.0F;
                    worldrenderer.pos(x + d9 * f10, y + d11 * (f10 * f10 + f10) * 0.5D + 0.25D, z + d12 * f10).color(0, 0, 0, 255).endVertex();
                }

                tessellator.draw();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                super.doRender(fish, x, y, z, entityYaw, partialTicks);
            }
        }
    }
}