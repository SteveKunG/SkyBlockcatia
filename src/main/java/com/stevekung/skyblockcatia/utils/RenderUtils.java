package com.stevekung.skyblockcatia.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.ColorUtils.RGB;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class RenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");

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
        compound.setTag("SkullOwner", properties);
        itemStack.setTagCompound(compound);
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

    public static void drawRarity(ItemStack itemStack, int xPos, int yPos)
    {
        if (itemStack != null && itemStack.hasTagCompound())
        {
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");

            if (compound.getTagId("Lore") == 9)
            {
                NBTTagList list = compound.getTagList("Lore", 8);

                if (list.tagCount() > 0)
                {
                    for (int j1 = 0; j1 < list.tagCount(); ++j1)
                    {
                        String lore = list.getStringTagAt(j1);
                        RGB common = ColorUtils.stringToRGB("255,255,255");
                        RGB uncommon = ColorUtils.stringToRGB("85,255,85");
                        RGB rare = ColorUtils.stringToRGB("85,85,255");
                        RGB epic = ColorUtils.stringToRGB("170,0,170");
                        RGB legendary = ColorUtils.stringToRGB("255,170,0");
                        RGB mythic = ColorUtils.stringToRGB("255,85,255");
                        RGB special = ColorUtils.stringToRGB("255,85,85");
                        RGB verySpecial = ColorUtils.stringToRGB("170,0,0");

                        if (RenderUtils.checkRarityString(lore, EnumChatFormatting.WHITE, "COMMON"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, common);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.GREEN, "UNCOMMON"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, uncommon);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.BLUE, "RARE"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, rare);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.DARK_PURPLE, "EPIC"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, epic);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.GOLD, "LEGENDARY"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, legendary);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.LIGHT_PURPLE, "MYTHIC"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, mythic);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.DARK_RED, "SUPREME"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, verySpecial);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.RED, "SPECIAL"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, special);
                        }
                        else if (RenderUtils.checkRarityString(lore, EnumChatFormatting.RED, "VERY SPECIAL"))
                        {
                            RenderUtils.renderRarity(xPos, yPos, verySpecial);
                        }
                    }
                }
            }
        }
    }

    public static void renderRarity(int xPos, int yPos, RGB color)
    {
        float alpha = ExtendedConfig.instance.itemRarityOpacity / 100.0F;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        RenderUtils.bindTexture(RARITY);
        GlStateManager.color(color.floatRed(), color.floatGreen(), color.floatBlue(), alpha);
        GlStateManager.blendFunc(770, 771);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 0, 0, 16, 16, 16, 16);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
    }

    private static boolean checkRarityString(String lore, EnumChatFormatting color, String text)
    {
        return lore.startsWith(color + "" + EnumChatFormatting.BOLD + text) || lore.startsWith(color + "" + EnumChatFormatting.BOLD + EnumChatFormatting.OBFUSCATED + "a" + EnumChatFormatting.RESET + " " + color + EnumChatFormatting.BOLD + color + EnumChatFormatting.BOLD + text);
    }
}