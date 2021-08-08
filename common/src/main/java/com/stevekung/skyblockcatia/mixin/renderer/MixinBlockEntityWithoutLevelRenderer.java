package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import net.minecraft.client.model.HumanoidHeadModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer
{
    private final SkullModel head = new HumanoidHeadModel();

    @Inject(method = "renderByItem", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.renderSkull(Lnet/minecraft/core/Direction;FLnet/minecraft/world/level/block/SkullBlock$Type;Lcom/mojang/authlib/GameProfile;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", shift = Shift.AFTER))
    private void renderDragonOverlay(ItemStack itemStack, ItemTransforms.TransformType type, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, CallbackInfo info)
    {
        if (itemStack.hasTag())
        {
            poseStack.pushPose();
            poseStack.translate(-0.5D, 0.0D, -0.5D);

            CompoundTag compound = itemStack.getTag().getCompound("ExtraAttributes");
            String id = compound.getString("id");
            ResourceLocation location = SBRenderUtils.getDragonEyeTexture(id);

            if (compound.contains("skin"))
            {
                location = SBRenderUtils.getDragonSkinTexture(id, compound.getString("skin"));
            }

            if (location != null)
            {
                poseStack.pushPose();
                poseStack.translate(1.0D, 0.0D, 1.0D);
                poseStack.scale(-1.001F, -1.0F, 1.001F);

                VertexConsumer ivertexbuilder = buffer.getBuffer(DragonArmorRenderType.getGlowingDragonOverlay(location));
                this.head.setupAnim(0.0F, 180.0F, 0.0F);
                this.head.renderToBuffer(poseStack, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }
}