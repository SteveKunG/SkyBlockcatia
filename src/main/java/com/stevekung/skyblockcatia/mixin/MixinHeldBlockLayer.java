package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldBlockLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

@Mixin(HeldBlockLayer.class)
public abstract class MixinHeldBlockLayer
{
    @SuppressWarnings("deprecation")
    @Redirect(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/monster/EndermanEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/BlockRendererDispatcher.renderBlock(Lnet/minecraft/block/BlockState;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V"))
    private void changeSpecialZealotBlock(BlockRendererDispatcher blockRenderer, BlockState blockState, MatrixStack matrixStack, IRenderTypeBuffer bufferType, int combinedLight, int combinedOverlay)
    {
        if (SBExtendedConfig.INSTANCE.makeSpecialZealotHeldGold && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd() && blockState.getBlock() == Blocks.END_PORTAL_FRAME)
        {
            blockState = Blocks.GOLD_BLOCK.getDefaultState();
            combinedOverlay = OverlayTexture.getPackedUV(OverlayTexture.getU(1.0F), 10);
        }
        blockRenderer.renderBlock(blockState, matrixStack, bufferType, combinedLight, combinedOverlay);
    }
}