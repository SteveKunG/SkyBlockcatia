package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerHeldBlock;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;

@Mixin(LayerHeldBlock.class)
public class LayerHeldBlockMixin
{
    @Redirect(method = "doRenderLayer(Lnet/minecraft/entity/monster/EntityEnderman;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/BlockRendererDispatcher.renderBlockBrightness(Lnet/minecraft/block/state/IBlockState;F)V"))
    private void changeSpecialZealotBlock(BlockRendererDispatcher blockrendererdispatcher, IBlockState state, float brightness)
    {
        if (SkyBlockcatiaSettings.INSTANCE.makeSpecialZealotHeldGold && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd() && state.getBlock() == Blocks.end_portal_frame)
        {
            state = Blocks.gold_block.getDefaultState();
        }
        blockrendererdispatcher.renderBlockBrightness(state, brightness);
    }

    @Redirect(method = "doRenderLayer(Lnet/minecraft/entity/monster/EntityEnderman;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/OpenGlHelper.setLightmapTextureCoords(IFF)V"))
    private void changeSpecialZealotLighting(int target, float p_77475_1_, float p_77475_2_, EntityEnderman entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        IBlockState state = entitylivingbaseIn.getHeldBlockState();

        if (SkyBlockcatiaSettings.INSTANCE.makeSpecialZealotHeldGold && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd() && state.getBlock().getMaterial() != Material.air && state.getBlock() == Blocks.end_portal_frame)
        {
            p_77475_1_ = 240.0F;
            p_77475_2_ = 240.0F;
        }
        OpenGlHelper.setLightmapTextureCoords(target, p_77475_1_, p_77475_2_);
    }
}