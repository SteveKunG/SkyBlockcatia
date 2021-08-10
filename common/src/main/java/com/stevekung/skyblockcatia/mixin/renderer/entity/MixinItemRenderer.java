package com.stevekung.skyblockcatia.mixin.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer
{
    @Inject(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
    private void renderItemModelIntoGUI(ItemStack itemStack, int x, int y, BakedModel bakedmodel, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.showItemRarity)
        {
            var poseStack = new PoseStack();
            poseStack.pushPose();
            SBRenderUtils.renderRarity(poseStack, itemStack, x, y);
            poseStack.popPose();
        }
    }
}