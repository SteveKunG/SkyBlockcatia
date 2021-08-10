package com.stevekung.skyblockcatia.mixin.fabric.renderer.entity.layers;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M>
{
    @Shadow
    abstract void setPartVisibility(A modelIn, EquipmentSlot slot);

    @Shadow
    abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow
    abstract A getArmorModel(EquipmentSlot slot);

    MixinHumanoidArmorLayer()
    {
        super(null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info)
    {
        this.renderGlowingLayer(poseStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.CHEST, packedLight);
        this.renderGlowingLayer(poseStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.LEGS, packedLight);
        this.renderGlowingLayer(poseStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.FEET, packedLight);
    }

    private void renderGlowingLayer(PoseStack poseStack, MultiBufferSource buffer, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlot slot, int packedLight)
    {
        if (SupportedPack.TYPE == null || !SupportedPack.FOUND || !SkyBlockcatiaSettings.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        var itemStack = entity.getItemBySlot(slot);

        if (itemStack.getItem() instanceof ArmorItem armorItem && itemStack.hasTag())
        {
            if (armorItem.getSlot() == slot)
            {
                var model = this.getArmorModel(slot);
                var location = this.getArmorType(itemStack.getTag().getCompound("ExtraAttributes").getString("id"), this.usesInnerModel(slot));
                this.getParentModel().copyPropertiesTo(model);
                model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                this.setPartVisibility(model, slot);
                model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                if (location != null)
                {
                    var time = entity.tickCount + partialTicks;
                    var alpha = 0.5F + (Mth.sin(time / 24) + 1F) / 3F + 0.05F;

                    if (alpha > 1.0F)
                    {
                        alpha = 1.0F;
                    }
                    var ivertexbuilder = ItemRenderer.getFoilBuffer(buffer, RenderType.eyes(location), false, false);
                    model.renderToBuffer(poseStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1.0F);
                }
            }
        }
    }

    private ResourceLocation getArmorType(String id, boolean isLeg)
    {
        var dragonType = DragonType.getDragonTypeById(id);
        return dragonType != null ? new ResourceLocation("skyblockcatia:textures/models/armor/" + SupportedPack.RESOLUTION + "/" + SupportedPack.TYPE.toLowerCase(Locale.ROOT) + "/" + dragonType.getShortName() + "_layer_" + (isLeg ? 2 : 1) + ".png") : null;
    }
}