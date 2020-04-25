package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin
{
    @Shadow
    @Final
    @Mutable
    protected Minecraft mc;

    @Inject(method = "func_181029_i()V", at = @At("RETURN"))
    private void resetToast(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().clear();
    }

    @Inject(method = "renderHotbarItem(IIIFLnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/entity/RenderItem.renderItemAndEffectIntoGUI(Lnet/minecraft/item/ItemStack;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderRarity(int index, int xPos, int yPos, float partialTicks, EntityPlayer player, CallbackInfo info, ItemStack itemstack, float f)
    {
        if (ExtendedConfig.instance.showItemRarity)
        {
            RenderUtils.drawRarity(itemstack, xPos, yPos);
        }
    }
}