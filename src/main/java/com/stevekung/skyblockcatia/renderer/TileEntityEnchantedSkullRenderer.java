package com.stevekung.skyblockcatia.renderer;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.event.ClientEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantedSkullRenderer
{
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
    public static final TileEntityEnchantedSkullRenderer INSTANCE = new TileEntityEnchantedSkullRenderer();

    public void renderSkull(float x, float y, float z, EnumFacing facing, float rotation, int meta, GameProfile profile, float partialTicks, boolean enchanted, @Nullable EntityLivingBase entity)
    {
        ModelBase model = this.skeletonHead;
        Minecraft mc = Minecraft.getMinecraft();

        switch (meta)
        {
        case 0:
        default:
            mc.getTextureManager().bindTexture(SKELETON_TEXTURES);
            break;
        case 1:
            mc.getTextureManager().bindTexture(WITHER_SKELETON_TEXTURES);
            break;
        case 2:
            mc.getTextureManager().bindTexture(ZOMBIE_TEXTURES);
            model = this.humanoidHead;
            break;
        case 3:
            model = this.humanoidHead;
            ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

            if (profile != null)
            {
                Map<Type, MinecraftProfileTexture> map = mc.getSkinManager().loadSkinFromCache(profile);

                if (map.containsKey(Type.SKIN))
                {
                    resourcelocation = mc.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
                }
                else
                {
                    UUID uuid = EntityPlayer.getUUID(profile);
                    resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                }
            }
            mc.getTextureManager().bindTexture(resourcelocation);
            break;
        case 4:
            mc.getTextureManager().bindTexture(CREEPER_TEXTURES);
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if (facing != EnumFacing.UP)
        {
            switch (facing)
            {
            case NORTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.74F);
                break;
            case SOUTH:
                GlStateManager.translate(x + 0.5F, y + 0.25F, z + 0.26F);
                rotation = 180.0F;
                break;
            case WEST:
                GlStateManager.translate(x + 0.74F, y + 0.25F, z + 0.5F);
                rotation = 270.0F;
                break;
            case EAST:
            default:
                GlStateManager.translate(x + 0.26F, y + 0.25F, z + 0.5F);
                rotation = 90.0F;
            }
        }
        else
        {
            GlStateManager.translate(x + 0.5F, y, z + 0.5F);
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();

        if (meta == 3 && ConfigManagerIN.enableTransparentSkinRender)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        model.render(null, 0.0F, 0.0F, 0.0F, rotation, 0.0F, 0.0625F);

        if (enchanted)
        {
            GlStateManager.pushMatrix();
            this.renderGlint(entity, mc, model, rotation, partialTicks);
            GlStateManager.popMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }
        GlStateManager.popMatrix();
    }

    private void renderGlint(EntityLivingBase entity, Minecraft mc, ModelBase model, float rotation, float partialTicks)
    {
        float f = ClientEventHandler.ticks + partialTicks;

        if (entity != null)
        {
            f = entity.ticksExisted + partialTicks;
        }

        mc.getTextureManager().bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(f1, f1, f1, 1.0F);

        if (entity != null && ConfigManagerIN.enable1_15ArmorEnchantedGlint)
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
            model.render(null, 0.0F, 0.0F, 0.0F, rotation, 0.0F, f);
        }

        if (entity != null && ConfigManagerIN.enable1_15ArmorEnchantedGlint)
        {
            int i = entity.getBrightnessForRender(partialTicks);
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
}