package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.*;

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
import stevekung.mods.indicatia.config.ConfigManagerIN;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.event.HypixelEventHandler;

@Mixin(LayerArmorBase.class)
public abstract class LayerArmorBaseMixin implements LayerRenderer<EntityLivingBase>
{
    private final LayerArmorBase that = (LayerArmorBase) (Object) this;

    @Shadow
    @Final
    @Mutable
    protected static ResourceLocation ENCHANTED_ITEM_GLINT_RES;

    @Shadow
    @Final
    @Mutable
    private RendererLivingEntity<?> renderer;

    @Shadow
    protected abstract void func_177179_a(ModelBase p_177179_1_, int p_177179_2_);

    @Shadow
    protected abstract ModelBase getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, int slot, ModelBase model);

    @Shadow
    protected abstract void renderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, int armorSlot);

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.renderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 4);
        this.renderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 3);
        this.renderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 2);
        this.renderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 1);
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 3);
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 2);
        this.renderGlowingLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, 1);
    }

    @Overwrite
    private void func_177183_a(EntityLivingBase entitylivingbaseIn, ModelBase modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_)
    {
        float f = entitylivingbaseIn.ticksExisted + p_177183_5_;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(f1, f1, f1, 1.0F);

        if (ConfigManagerIN.enable1_15ArmorEnchantedGlint)
        {
            float light = 128.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light, light);
        }

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(ConfigManagerIN.enable1_15ArmorEnchantedGlint ? 770 : 768, 1);
            GlStateManager.color(0.38F, 0.19F, 0.608F, ConfigManagerIN.enable1_15ArmorEnchantedGlint ? 0.5F : 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334F;
            GlStateManager.scale(f3, f3, f3);
            GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
        }

        if (ConfigManagerIN.enable1_15ArmorEnchantedGlint)
        {
            int i = entitylivingbaseIn.getBrightnessForRender(p_177183_5_);
            int j = i % 65536;
            int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }

    private void renderGlowingLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, int armorSlot)
    {
        if (!HypixelEventHandler.foundSkyBlockPack || !ExtendedConfig.instance.glowingDragonArmor)
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

            ModelBase t = this.that.func_177175_a(armorSlot);
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

    @Override
    public boolean shouldCombineTextures()
    {
        return ConfigManagerIN.enableOldArmorRender;
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

        String s1 = String.format("indicatia:textures/model/armor/" + HypixelEventHandler.skyBlockPackResolution + "/%s_layer_%d.png", texture, armorSlot == 2 ? 2 : 1);
        return texture.isEmpty() ? null : new ResourceLocation(s1);
    }
}