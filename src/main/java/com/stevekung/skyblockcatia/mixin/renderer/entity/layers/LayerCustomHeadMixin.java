package com.stevekung.skyblockcatia.mixin.renderer.entity.layers;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.renderer.GlowingCustomHead;
import com.stevekung.skyblockcatia.renderer.TileEntityEnchantedSkullRenderer;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;

import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

@Mixin(LayerCustomHead.class)
public class LayerCustomHeadMixin
{
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

    @Shadow
    @Final
    private ModelRenderer field_177209_a;

    @Inject(method = "doRenderLayer", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/tileentity/TileEntitySkullRenderer.renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;I)V"))
    private void renderEnchantedSkull(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info)
    {
        ItemStack itemStack = entity.getCurrentArmor(3);

        if (SkyBlockcatiaConfig.enableEnchantedGlintForSkull && !CompatibilityUtils.hasDisableEnchantmentGlint())
        {
            TileEntityEnchantedSkullRenderer.INSTANCE.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemStack.getMetadata(), GlowingCustomHead.getGameProfile(itemStack), -1, itemStack.hasEffect(), entity);
            GlStateManager.popMatrix();
            info.cancel();
        }
    }

    @Inject(method = "doRenderLayer", at = @At("HEAD"))
    private void renderGlowingLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info)
    {
        GlowingCustomHead.renderGlowingArmor(this.field_177209_a, this.humanoidHead, entity, partialTicks);
    }
}