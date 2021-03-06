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
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.skyblock.SBRarity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
    private static final Pattern PET_PATTERN = Pattern.compile("\\u00a77\\[Lvl \\d+\\] (?<color>\\u00a7[0-9a-fk-or]).+");

    public static void renderItem(ItemStack itemStack, int x, int y)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
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
            String id = extra.getString("id");
            boolean upgrade = extra.hasKey("rarity_upgrades");

            if (extra.hasKey("id"))
            {
                if (id.equals("PARTY_HAT_CRAB"))
                {
                    SBRarity rarity = upgrade ? SBRarity.COMMON.getNextRarity() : SBRarity.COMMON;
                    RenderUtils.renderRarity(xPos, yPos, rarity);
                    return;
                }
                if (id.equals("SKYBLOCK_MENU") || id.contains("GENERATOR") || id.contains("RUNE"))
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
                                    RenderUtils.renderRarity(xPos, yPos, SBRarity.byBaseColor(lore.charAt(0) + "" + lore.charAt(1)));
                                }
                            }
                        }
                    }
                    return;
                }

                if (displayName.startsWith("\u00a7f\u00a7f"))
                {
                    displayName = displayName.substring(4);

                    if (displayName.matches("\\u00a7[0-9a-fk-or]\\d.*"))
                    {
                        displayName = displayName.replaceAll("\\u00a7[0-9a-fk-or]\\d.*x \\u00a7f", "");
                    }
                }

                Matcher mat = PATTERN.matcher(displayName);

                if (mat.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SBRarity.byBaseColor(mat.group("color")));
                }

                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SBRarity.byBaseColor(mat1.group("color")));
                }
            }
            else
            {
                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    RenderUtils.renderRarity(xPos, yPos, SBRarity.byBaseColor(mat1.group("color")));
                }
            }
        }
    }

    private static void renderRarity(int xPos, int yPos, SBRarity rarity)
    {
        if (rarity != null)
        {
            float alpha = SkyBlockcatiaSettings.INSTANCE.itemRarityOpacity / 100.0F;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            Minecraft.getMinecraft().getTextureManager().bindTexture(RARITY);
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