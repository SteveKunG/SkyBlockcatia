package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin
{
    @Redirect(method = "renderItemModelForEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", at = @At(value = "NEW", target = "net/minecraft/client/resources/model/ModelResourceLocation", ordinal = 0))
    private ModelResourceLocation setNewFishingRodModel(String p_i46081_1_, String p_i46081_2_)
    {
        return ConfigManagerIN.enableOldFishingRodRenderModel ? new ModelResourceLocation("skyblockcatia:fishing_rod_cast", "inventory") : new ModelResourceLocation(p_i46081_1_, p_i46081_2_);
    }

    @Inject(method = "renderItemIntoGUI(Lnet/minecraft/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderRarity(ItemStack itemStack, int xPosition, int yPosition, CallbackInfo info)
    {
        if (ExtendedConfig.instance.showItemRarity)
        {
            RenderUtils.renderRarity(itemStack, xPosition, yPosition);
        }
    }

    @ModifyConstant(method = "renderEffect(Lnet/minecraft/client/resources/model/IBakedModel;)V", constant = @Constant(intValue = -8372020))
    private int setGlintColor(int defaultValue)
    {
        return ConfigManagerIN.enable1_15ArmorEnchantedGlint ? -7383333 : defaultValue;
    }
}