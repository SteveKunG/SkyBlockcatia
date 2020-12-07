package com.stevekung.skyblockcatia.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerGlowingSteveKunG implements LayerRenderer<EntityLivingBase>
{
    private final RenderPlayer playerRenderer;

    public LayerGlowingSteveKunG(RenderPlayer playerRenderer)
    {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!entity.isInvisible() && entity.getName().equals("SteveKunG"))
        {
            GlStateManager.pushMatrix();
            ResourceLocation resource = new ResourceLocation("skyblockcatia:textures/entity/stevekung.png");

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 86.0F, 86.0F);
            GlStateManager.enableLighting();

            float time = entity.getEntityId() + entity.ticksExisted + partialTicks;
            float sin = (MathHelper.sin(time / 24) + 1.25F) / 2F + 0.15F;
            GlStateManager.color(sin, sin, sin, sin);
            Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
            this.playerRenderer.getMainModel().render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            int i = entity.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return false;
    }
}