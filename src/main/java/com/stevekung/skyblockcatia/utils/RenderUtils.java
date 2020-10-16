package com.stevekung.skyblockcatia.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.ExtendedConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class RenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");
    private static final Pattern PATTERN = Pattern.compile("(?<color>\\u00a7[0-9a-fk-or]).+");
    private static final Pattern PET_PATTERN = Pattern.compile("\\u00a77\\[Lvl \\d+\\] (?<color>\\u00a7[0-9a-fk-or])[\\w ]+");

    public static void bindTexture(ResourceLocation resource)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static void bindTexture(String resource)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(resource));
    }

    public static void disableLighting()
    {
        GlStateManager.disableLighting();
        GlStateManager.disableLight(0);
        GlStateManager.disableLight(1);
        GlStateManager.disableColorMaterial();
    }

    public static void enableLighting()
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
    }

    public static ItemStack getSkullItemStack(String skullId, String skullValue)
    {
        ItemStack itemStack = new ItemStack(Items.skull, 1, 3);
        return RenderUtils.setSkullSkin(itemStack, skullId, skullValue);
    }

    public static ItemStack setSkullSkin(ItemStack itemStack, String skullId, String skullValue)
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound properties = new NBTTagCompound();
        properties.setString("Id", skullId);
        NBTTagCompound texture = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.setString("Value", RenderUtils.toSkullURL(skullValue));
        list.appendTag(value);
        texture.setTag("textures", list);
        properties.setTag("Properties", texture);

        if (!itemStack.hasTagCompound())
        {
            compound.setTag("SkullOwner", properties);
            itemStack.setTagCompound(compound);
        }
        else
        {
            itemStack.getTagCompound().setTag("SkullOwner", properties);
        }

        return itemStack;
    }

    public static String decodeTextureURL(String source)
    {
        JsonObject obj = new JsonParser().parse(new String(Base64.getDecoder().decode(source))).getAsJsonObject();
        String textureurl = obj.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
        return textureurl.substring(textureurl.lastIndexOf("/") + 1);
    }

    private static String toSkullURL(String url)
    {
        JsonObject skin = new JsonObject();
        skin.addProperty("url", "http://textures.minecraft.net/texture/" + url);
        JsonObject textures = new JsonObject();
        textures.add("SKIN", skin);
        JsonObject root = new JsonObject();
        root.add("textures", textures);
        return Base64.getEncoder().encodeToString(new Gson().toJson(root).getBytes(StandardCharsets.UTF_8));
    }

    public static ItemStack getPlayerHead(String name)
    {
        ItemStack itemStack = new ItemStack(Items.skull, 1, 3);
        NBTTagCompound compound = new NBTTagCompound();
        GameProfile profile = TileEntitySkull.updateGameprofile(new GameProfile(null, name));
        compound.removeTag("SkullOwner");
        compound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
        itemStack.setTagCompound(compound);
        return itemStack;
    }

    public static void renderRarity(ItemStack itemStack, int xPos, int yPos)
    {
        if (itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");
            NBTTagCompound extra = itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            String displayName = compound.getString("Name");

            if (extra.hasKey("id"))
            {
                if (extra.getString("id").equals("PARTY_HAT_CRAB"))
                {
                    RenderUtils.renderRarity(xPos, yPos, SkyBlockRarity.COMMON);
                }
                if (extra.getString("id").equals("SKYBLOCK_MENU") || extra.getString("id").contains("GENERATOR") || extra.getString("id").contains("RUNE"))
                {
                    if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                    {
                        NBTTagList list = compound.getTagList("Lore", Constants.NBT.TAG_STRING);

                        if (list.tagCount() > 0)
                        {
                            for (int j1 = 0; j1 < list.tagCount(); ++j1)
                            {
                                String lore = list.getStringTagAt(j1);

                                if (lore.contains("COSMETIC")) // temp
                                {
                                    RenderUtils.renderRarity(xPos, yPos, SkyBlockRarity.byBaseColor(lore.charAt(0) + "" + lore.charAt(1)));
                                }
                            }
                        }
                    }
                    return;
                }

                Matcher mat = PATTERN.matcher(displayName);

                if (mat.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SkyBlockRarity.byBaseColor(mat.group("color")));
                }

                if (displayName.startsWith("\u00a7f\u00a7f"))
                {
                    displayName = displayName.substring(4);
                }

                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SkyBlockRarity.byBaseColor(mat1.group("color")));
                }
            }
            else
            {
                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SkyBlockRarity.byBaseColor(mat1.group("color")));
                }
            }
        }
    }

    private static void renderRarity(int xPos, int yPos, SkyBlockRarity rarity)
    {
        if (rarity != null)
        {
            float alpha = ExtendedConfig.instance.itemRarityOpacity / 100.0F;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            RenderUtils.bindTexture(RARITY);
            GlStateManager.color(rarity.getColorToRender().floatRed(), rarity.getColorToRender().floatGreen(), rarity.getColorToRender().floatBlue(), alpha);
            GlStateManager.blendFunc(770, 771);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
            Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 0, 0, 16, 16, 16, 16);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            GlStateManager.disableAlpha();
        }
    }
}