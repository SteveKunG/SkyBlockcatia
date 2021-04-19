package com.stevekung.skyblockcatia.mixin.renderer.entity.layers;

import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.SupportedPack;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@Mixin(LayerArmorBase.class)
public abstract class LayerArmorBaseMixin<T extends ModelBase> implements LayerRenderer<EntityLivingBase>
{
    private final LayerArmorBase that = (LayerArmorBase) (Object) this;

    @Shadow
    @Final
    protected static ResourceLocation ENCHANTED_ITEM_GLINT_RES;

    @Shadow
    @Final
    private RendererLivingEntity<?> renderer;

    @Shadow
    protected abstract void func_177179_a(T p_177179_1_, int p_177179_2_);

    @Shadow
    public abstract T func_177175_a(int p_177175_1_);

    @Shadow
    protected abstract T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, int slot, T model);

    @Shadow
    protected abstract void renderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, int armorSlot);

    @Inject(method = "doRenderLayer(Lnet/minecraft/entity/EntityLivingBase;FFFFFFF)V", at = @At("RETURN"))
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo info)
    {
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 3);
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 2);
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 1);
    }

    @Inject(method = "func_177183_a(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.color(FFFF)V", ordinal = 0))
    private void renderNewArmorGlintPre(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enable1_15ArmorEnchantedGlint)
        {
            float light = 240.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
        }
    }

    @Inject(method = "func_177183_a(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/model/ModelBase.render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderNewArmorGlintPost(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enable1_15ArmorEnchantedGlint)
        {
            int i = entitylivingbaseIn.getBrightnessForRender(p_177183_5_);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
        }
    }

    @Redirect(method = "func_177183_a(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.color(FFFF)V", ordinal = 1))
    private void newArmorGlintColor(float colorRed, float colorGreen, float colorBlue, float colorAlpha)
    {
        if (SkyBlockcatiaConfig.enable1_15ArmorEnchantedGlint)
        {
            GlStateManager.color(0.5608F, 0.3408F, 0.8608F, 1.0F);
        }
        else
        {
            GlStateManager.color(colorRed, colorGreen, colorBlue, colorAlpha);
        }
    }

    private void renderGlowingLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, int armorSlot)
    {
        if (SupportedPack.TYPE == null || !SupportedPack.FOUND || !SkyBlockcatiaSettings.INSTANCE.glowingDragonArmor)
        {
            return;
        }

        ItemStack itemstack = this.that.getCurrentArmor(entity, armorSlot);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor)
        {
            if (!itemstack.hasTagCompound())
            {
                return;
            }

            T t = this.func_177175_a(armorSlot);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            t = this.getArmorModelHook(entity, itemstack, armorSlot, t);
            this.func_177179_a(t, armorSlot);
            ResourceLocation res = this.getArmorType(itemstack, armorSlot);

            if (res == null)
            {
                return;
            }

            this.renderer.bindTexture(res);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(!entity.isInvisible());
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 128.0F, 128.0F);
            GlStateManager.enableLighting();

            float time = entity.getEntityId() + entity.ticksExisted + partialTicks;
            float sin = (MathHelper.sin(time / 16) + 1F) / 1.5F + 0.15F;
            GlStateManager.color(sin, sin, sin, sin);
            t.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            int i = entity.getBrightnessForRender(partialTicks);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
        }
    }

    private ResourceLocation getArmorType(ItemStack itemStack, int armorSlot)
    {
        String texture = "";
        String id = itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id");

        if (id.startsWith("SUPERIOR_DRAGON"))
        {
            texture = "superior";
        }
        else if (id.startsWith("WISE_DRAGON"))
        {
            texture = "wise";
        }
        else if (id.startsWith("YOUNG_DRAGON"))
        {
            texture = "young";
        }
        else if (id.startsWith("OLD_DRAGON"))
        {
            texture = "old";
        }
        else if (id.startsWith("PROTECTOR_DRAGON"))
        {
            texture = "protector";
        }
        else if (id.startsWith("UNSTABLE_DRAGON"))
        {
            texture = "unstable";
        }
        else if (id.startsWith("STRONG_DRAGON"))
        {
            texture = "strong";
        }
        else if (id.startsWith("HOLY_DRAGON"))
        {
            texture = "holy";
        }

        String s1 = String.format("skyblockcatia:textures/model/armor/" + SupportedPack.RESOLUTION + "/" + SupportedPack.TYPE.toLowerCase(Locale.ROOT) + "/%s_layer_%d.png", texture, armorSlot == 2 ? 2 : 1);
        return texture.isEmpty() ? null : new ResourceLocation(s1);
    }
}