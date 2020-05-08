package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(IngameGui.class)
public abstract class MixinIngameGui
{
    @Inject(method = "renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ItemRenderer.renderItemAndEffectIntoGUI(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    private void renderRarity(int x, int y, float partialTicks, PlayerEntity player, ItemStack itemStack, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.showItemRarity)
        {
            SBRenderUtils.renderRarity(itemStack, x, y);
        }
    }
}