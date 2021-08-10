package com.stevekung.skyblockcatia.mixin.renderer;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class MixinBlockEntityWithoutLevelRenderer
{
    @Shadow
    Map<SkullBlock.Type, SkullModelBase> skullModels;

    @Inject(method = "renderByItem", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/blockentity/SkullBlockRenderer.renderSkull(Lnet/minecraft/core/Direction;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/SkullModelBase;Lnet/minecraft/client/renderer/RenderType;)V", shift = Shift.AFTER))
    private void renderDragonOverlay(ItemStack itemStack, ItemTransforms.TransformType type, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, CallbackInfo info)
    {
        if (itemStack.hasTag())
        {
            poseStack.pushPose();
            poseStack.translate(-0.5D, 0.0D, -0.5D);

            var compound = itemStack.getTag().getCompound("ExtraAttributes");
            var id = compound.getString("id");
            var location = SBRenderUtils.getDragonEyeTexture(id);

            if (compound.contains("skin"))
            {
                location = SBRenderUtils.getDragonSkinTexture(id, compound.getString("skin"));
            }

            if (location != null)
            {
                poseStack.pushPose();
                poseStack.translate(1.0D, 0.0D, 1.0D);
                poseStack.scale(-1.001F, -1.0F, 1.001F);

                var ivertexbuilder = buffer.getBuffer(RenderType.eyes(location));
                this.skullModels.get(SkullBlock.Types.PLAYER).setupAnim(0.0F, 180.0F, 0.0F);
                this.skullModels.get(SkullBlock.Types.PLAYER).renderToBuffer(poseStack, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
            poseStack.popPose();
        }
    }
}