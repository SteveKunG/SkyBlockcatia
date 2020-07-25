package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@Mixin(TileEntityItemStackRenderer.class)
public abstract class TileEntityItemStackRendererMixin
{
    @Redirect(method = "renderByItem(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/tileentity/TileEntitySkullRenderer.renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;I)V"))
    private void renderEnchantedSkull(TileEntitySkullRenderer renderer, float x, float y, float z, EnumFacing facing, float rotation, int meta, GameProfile profile, int destroyStages, ItemStack itemStack)
    {
        TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, itemStack.getMetadata(), profile, Minecraft.getMinecraft().timer.renderPartialTicks, itemStack.hasEffect(), null);
    }
}