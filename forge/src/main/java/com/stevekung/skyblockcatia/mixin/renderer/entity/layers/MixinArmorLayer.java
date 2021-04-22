package com.stevekung.skyblockcatia.mixin.renderer.entity.layers;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.renderer.DragonArmorRenderType;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.api.DragonType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

@Mixin(HumanoidArmorLayer.class)
public abstract class MixinArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M>
{
    @Shadow
    protected abstract void setPartVisibility(A modelIn, EquipmentSlot slot);

    @Shadow(remap = false)
    protected abstract A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model);

    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot slot);

    @Shadow
    private A getArmorModel(EquipmentSlot slot)
    {
        return null;
    }

    MixinArmorLayer()
    {
        super(null);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info)
    {
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.CHEST, packedLight);
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.LEGS, packedLight);
        this.renderGlowingLayer(matrixStack, buffer, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlot.FEET, packedLight);
    }

    private void renderGlowingLayer(PoseStack matrixStack, MultiBufferSource buffer, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlot slot, int packedLight)
    {
        if (SupportedPack.TYPE == null || !SupportedPack.FOUND || !SkyBlockcatiaSettings.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemStack = entity.getItemBySlot(slot);

        if (itemStack.getItem() instanceof ArmorItem && itemStack.hasTag())
        {
            ArmorItem armorItem = (ArmorItem)itemStack.getItem();

            if (armorItem.getSlot() == slot)
            {
                A model = this.getArmorModel(slot);
                ResourceLocation location = this.getArmorType(itemStack.getTag().getCompound("ExtraAttributes").getString("id"), this.usesInnerModel(slot));
                model = this.getArmorModelHook(entity, itemStack, slot, model);
                this.getParentModel().copyPropertiesTo(model);
                model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                this.setPartVisibility(model, slot);
                model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                if (location != null)
                {
                    float time = entity.tickCount + partialTicks;
                    float alpha = 0.5F + (Mth.sin(time / 24) + 1F) / 3F + 0.05F;

                    if (alpha > 1.0F)
                    {
                        alpha = 1.0F;
                    }
                    VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(buffer, DragonArmorRenderType.getGlowingDragonOverlay(location), false, false);
                    model.renderToBuffer(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, 1.0F);
                }
            }
        }
    }

    private ResourceLocation getArmorType(String id, boolean isLeg)
    {
        DragonType dragonType = DragonType.getDragonTypeById(id);
        return dragonType != null ? new ResourceLocation("skyblockcatia:textures/models/armor/" + SupportedPack.RESOLUTION + "/" + SupportedPack.TYPE.toLowerCase(Locale.ROOT) + "/" + dragonType.getShortName() + "_layer_" + (isLeg ? 2 : 1) + ".png") : null;
    }
}