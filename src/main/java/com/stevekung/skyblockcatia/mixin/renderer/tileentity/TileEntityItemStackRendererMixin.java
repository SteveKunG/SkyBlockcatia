package com.stevekung.skyblockcatia.mixin.renderer.tileentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@Mixin(TileEntityItemStackRenderer.class)
public class TileEntityItemStackRendererMixin
{
    @Inject(method = "renderByItem(Lnet/minecraft/item/ItemStack;)V", cancellable = true, at = @At(value = "FIELD", target = "net/minecraft/client/renderer/tileentity/TileEntitySkullRenderer.instance:Lnet/minecraft/client/renderer/tileentity/TileEntitySkullRenderer;", ordinal = 0, shift = Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderEnchantedSkull(ItemStack itemStack, CallbackInfo info, GameProfile gameprofile)
    {
        if (SkyBlockcatiaConfig.enableEnchantedGlintForSkull && !CompatibilityUtils.hasDisableEnchantmentGlint())
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.disableCull();
            TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStack.getMetadata(), gameprofile, Minecraft.getMinecraft().timer.renderPartialTicks, itemStack.hasEffect(), null);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            info.cancel();
        }
    }
}