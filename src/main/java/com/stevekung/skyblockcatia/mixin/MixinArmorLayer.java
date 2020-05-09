package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@Mixin(ArmorLayer.class)
public abstract class MixinArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M>
{
    @Shadow
    public abstract A getModelFromSlot(EquipmentSlotType slot);

    @Shadow
    protected abstract void setModelSlotVisible(A modelIn, EquipmentSlotType slot);

    @Shadow(remap = false)
    protected abstract A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model);

    @Shadow
    protected abstract boolean isLegSlot(EquipmentSlotType slot);

    public MixinArmorLayer(IEntityRenderer<T, M> renderer)
    {
        super(renderer);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("RETURN"))
    private void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info)
    {
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.CHEST, packedLight);
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.LEGS, packedLight);
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.FEET, packedLight);
    }

    private void renderGlowingLayer(MatrixStack matrixStack, IRenderTypeBuffer buffer, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlotType slot, int packedLight)
    {
        if (!SkyBlockEventHandler.foundSkyBlockPack || !SBExtendedConfig.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemStack = entity.getItemStackFromSlot(slot);

        if (itemStack.getItem() instanceof ArmorItem)
        {
            ArmorItem armorItem = (ArmorItem)itemStack.getItem();

            if (itemStack.isEmpty() || !itemStack.hasTag())
            {
                return;
            }

            if (armorItem.getEquipmentSlot() == slot)
            {
                A model = this.getModelFromSlot(slot);
                ResourceLocation location = this.getArmorType(itemStack.getTag().getCompound("ExtraAttributes").getString("id"), this.isLegSlot(slot));
                model = this.getArmorModelHook(entity, itemStack, slot, model);
                ((BipedModel)this.getEntityModel()).setModelAttributes(model);
                model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
                this.setModelSlotVisible(model, slot);
                model.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                if (location != null)
                {
                    float time = entity.ticksExisted + partialTicks;
                    float alpha = 0.5F + (MathHelper.sin(time / 24) + 1F) / 3F + 0.05F;

                    if (alpha > 1.0F)
                    {
                        alpha = 1.0F;
                    }
                    IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(buffer, DragonArmorRenderType.getGlowingDragonOverlay(location), false, false);
                    model.render(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1.0F);
                }
            }
        }
    }

    private ResourceLocation getArmorType(String id, boolean isLeg)
    {
        DragonType dragonType = DragonType.getDragonTypeById(id);
        return dragonType != null ? new ResourceLocation("skyblockcatia:textures/models/armor/" + SkyBlockEventHandler.skyBlockPackResolution + "/" + dragonType.getShortName() + "_layer_" + String.valueOf(isLeg ? 2 : 1) + ".png") : null;
    }
}