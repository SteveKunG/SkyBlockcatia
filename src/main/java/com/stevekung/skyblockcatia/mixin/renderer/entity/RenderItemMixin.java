package com.stevekung.skyblockcatia.mixin.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

@Mixin(RenderItem.class)
public class RenderItemMixin
{
    @Inject(method = "renderItemIntoGUI(Lnet/minecraft/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderRarity(ItemStack itemStack, int xPosition, int yPosition, CallbackInfo info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.showItemRarity)
        {
            RenderUtils.renderRarity(itemStack, xPosition, yPosition);
        }
    }
}