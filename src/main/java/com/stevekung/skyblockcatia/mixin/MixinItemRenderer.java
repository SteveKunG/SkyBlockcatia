package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer
{
    @Inject(method = "renderItemModelIntoGUI(Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/renderer/model/IBakedModel;)V", at = @At("HEAD"))
    private void renderItemModelIntoGUI(ItemStack itemStack, int x, int y, IBakedModel bakedmodel, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.showItemRarity)
        {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.push();
            SBRenderUtils.renderRarity(matrixStack, itemStack, x, y);
            matrixStack.pop();
        }
    }
} 