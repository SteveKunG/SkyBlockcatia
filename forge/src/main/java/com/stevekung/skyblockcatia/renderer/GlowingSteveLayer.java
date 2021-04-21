package com.stevekung.skyblockcatia.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GlowingSteveLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/entity/stevekung.png");

    public GlowingSteveLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!entity.isInvisible() && entity.getName().getString().equals("SteveKunG"))
        {
            float time = entity.tickCount + partialTicks;
            float alpha = (Mth.sin(time / 24) + 1F) / 2F + 0.15F;

            if (alpha > 0.7F)
            {
                alpha = 0.7F;
            }
            VertexConsumer ivertexbuilder = buffer.getBuffer(DragonArmorRenderType.getGlowingDragonOverlay(GlowingSteveLayer.TEXTURE));
            this.getParentModel().renderToBuffer(matrixStack, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1.0F);
        }
    }
}