package com.stevekung.skyblockcatia.mixin.optifine.renderer.tileentity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@Mixin(ItemStackTileEntityRenderer.class)
public class MixinItemStackTileEntityRendererOptifine
{
    private final GenericHeadModel head = new HumanoidHeadModel();

    @Inject(method = "renderRaw(Lnet/minecraft/item/ItemStack;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;II)V", remap = false, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/tileentity/SkullTileEntityRenderer.func_228879_a_(Lnet/minecraft/util/Direction;FLnet/minecraft/block/SkullBlock$ISkullType;Lcom/mojang/authlib/GameProfile;FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", remap = false, shift = Shift.AFTER))
    private void renderDragonOverlay(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, CallbackInfo info)
    {
        if (itemStack.hasTag())
        {
            matrixStack.push();
            matrixStack.translate(-0.5D, 0.0D, -0.5D);

            CompoundNBT compound = itemStack.getTag().getCompound("ExtraAttributes");
            String id = compound.getString("id");
            ResourceLocation location = SBRenderUtils.getDragonEyeTexture(id);

            if (compound.contains("skin"))
            {
                location = SBRenderUtils.getDragonSkinTexture(id, compound.getString("skin"));
            }

            if (location != null)
            {
                matrixStack.push();
                matrixStack.translate(1.0D, 0.0D, 1.0D);
                matrixStack.scale(-1.001F, -1.0F, 1.001F);

                IVertexBuilder ivertexbuilder = buffer.getBuffer(DragonArmorRenderType.getGlowingDragonOverlay(location));
                this.head.func_225603_a_(0.0F, 180.0F, 0.0F);
                this.head.render(matrixStack, ivertexbuilder, combinedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStack.pop();
            }
            matrixStack.pop();
        }
    }
}