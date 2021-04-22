package com.stevekung.skyblockcatia.mixin.renderer.entity.layers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(CarriedBlockLayer.class)
public class MixinHeldBlockLayer
{
    @SuppressWarnings("deprecation")
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/block/BlockRenderDispatcher.renderSingleBlock(Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"))
    private void changeSpecialZealotBlock(BlockRenderDispatcher blockRenderer, BlockState blockState, PoseStack matrixStack, MultiBufferSource bufferType, int combinedLight, int combinedOverlay)
    {
        if (SkyBlockcatiaSettings.INSTANCE.makeSpecialZealotHeldGold && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd() && blockState.getBlock() == Blocks.END_PORTAL_FRAME)
        {
            blockState = Blocks.GOLD_BLOCK.defaultBlockState();
            combinedOverlay = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
        }
        blockRenderer.renderSingleBlock(blockState, matrixStack, bufferType, combinedLight, combinedOverlay);
    }
}