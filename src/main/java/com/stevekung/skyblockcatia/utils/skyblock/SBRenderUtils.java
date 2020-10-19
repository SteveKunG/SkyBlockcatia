package com.stevekung.skyblockcatia.utils.skyblock;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class SBRenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");
    private static final Pattern PATTERN = Pattern.compile("(?<color>\\u00a7[0-9a-fk-or]).+");
    private static final Pattern PET_PATTERN = Pattern.compile("\\u00a77\\[Lvl \\d+\\] (?<color>\\u00a7[0-9a-fk-or]).+");

    public static ItemStack getSkullItemStack(String skullId, String skullValue)
    {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        return SBRenderUtils.setSkullSkin(itemStack, skullId, skullValue);
    }

    public static ItemStack setSkullSkin(ItemStack itemStack, String skullId, String skullValue)
    {
        CompoundNBT compound = new CompoundNBT();
        CompoundNBT properties = new CompoundNBT();
        properties.putIntArray("Id", SBRenderUtils.uuidToIntArray(skullId));
        CompoundNBT texture = new CompoundNBT();
        ListNBT list = new ListNBT();
        CompoundNBT value = new CompoundNBT();
        value.putString("Value", SBRenderUtils.toSkullURL(skullValue));
        list.add(value);
        texture.put("textures", list);
        properties.put("Properties", texture);
        compound.put("SkullOwner", properties);
        itemStack.setTag(compound);

        if (!itemStack.hasTag())
        {
            compound.put("SkullOwner", properties);
            itemStack.setTag(compound);
        }
        else
        {
            itemStack.getTag().put("SkullOwner", properties);
        }

        return itemStack;
    }

    public static ItemStack getPlayerHead(String name)
    {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        CompoundNBT compound = new CompoundNBT();
        GameProfile profile = SkullTileEntity.updateGameProfile(new GameProfile(null, name));
        compound.remove("SkullOwner");
        compound.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
        itemStack.setTag(compound);
        return itemStack;
    }

    public static int[] uuidToIntArray(String id)
    {
        UUID uuid = UUID.fromString(id);
        long uuidMost = uuid.getMostSignificantBits();
        long uuidLeast = uuid.getLeastSignificantBits();
        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
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

    public static void renderRarity(MatrixStack matrixStack, ItemStack itemStack, int xPos, int yPos)
    {
        if (!itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT compound = itemStack.getTag().getCompound("display");
            CompoundNBT extra = itemStack.getTag().getCompound("ExtraAttributes");
            String displayName = compound.getString("Name");
            String id = extra.getString("id");
            boolean upgrade = extra.contains("rarity_upgrades");

            IFormattableTextComponent component = TextComponentUtils.fromJson(displayName);

            if (component != null)
            {
                displayName = component.getString();
            }

            if (extra.contains("id"))
            {
                if (id.equals("PARTY_HAT_CRAB"))
                {
                    SBRarity rarity = upgrade ? SBRarity.COMMON.getNextRarity() : SBRarity.COMMON;
                    SBRenderUtils.renderRarity(matrixStack, xPos, yPos, rarity);
                    return;
                }
                if (id.equals("SKYBLOCK_MENU") || id.contains("GENERATOR") || id.contains("RUNE"))
                {
                    if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                    {
                        ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                        for (int j1 = 0; j1 < list.size(); ++j1)
                        {
                            String lore = TextFormatting.getTextWithoutFormattingCodes(TextComponentUtils.fromJson(list.getString(j1)).getString());

                            if (lore.contains("COSMETIC")) // temp
                            {
                                SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(lore.charAt(0) + "" + lore.charAt(1)));
                            }
                        }
                    }
                    return;
                }

                if (displayName.startsWith("\u00a7f\u00a7f"))
                {
                    displayName = displayName.substring(4);
                }

                Matcher mat = PATTERN.matcher(displayName);

                if (mat.matches())
                {
                    SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(mat.group("color")));
                }

                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(mat1.group("color")));
                }
            }
            else
            {
                Matcher mat1 = PET_PATTERN.matcher(displayName);

                if (mat1.matches())
                {
                    SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(mat1.group("color")));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void renderRarity(MatrixStack matrixStack, int xPos, int yPos, SBRarity rarity)
    {
        if (rarity != null)
        {
            float alpha = SBExtendedConfig.INSTANCE.itemRarityOpacity / 100.0F;
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderUtils.bindTexture(RARITY);
            RenderSystem.color4f(rarity.getColorToRender()[0], rarity.getColorToRender()[1], rarity.getColorToRender()[2], alpha);
            RenderSystem.blendFunc(770, 771);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
            AbstractGui.blit(matrixStack, xPos, yPos, 0, 0, 16, 16, 16, 16);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
            RenderSystem.disableAlphaTest();
        }
    }

    // Credit to https://gist.github.com/killjoy1221/71b4cd975b92afe8dbd2e5f6222b1140
    public static void drawLongItemName(ToastGui toastGui, MatrixStack matrixStack, long delta, long firstDrawTime, long maxDrawTime, String itemName, boolean shadow)
    {
        long minDraw = (long)(maxDrawTime * 0.1D);
        long maxDraw = maxDrawTime + 500L;
        long backwardDraw = (long)(maxDrawTime * 0.5D);
        long textSpeed = 1500L + (long)(maxDrawTime * 0.1D);
        int x = 30;
        int textWidth = toastGui.getMinecraft().fontRenderer.getStringWidth(itemName);
        int maxSize = textWidth - 135;
        long timeElapsed = delta - firstDrawTime - minDraw;
        long timeElapsed2 = maxDraw - delta - backwardDraw;
        int maxTextLength = 125;

        if (textWidth > maxSize && textWidth > maxTextLength)
        {
            if (timeElapsed > 0)
            {
                x = Math.max((int) (-textWidth * timeElapsed / textSpeed + x), -maxSize + 16);
            }

            int backward = Math.max(Math.min((int) -(textWidth * timeElapsed2 / textSpeed), 30), -maxSize + 16);

            if (timeElapsed > timeElapsed2)
            {
                x = backward;
            }
        }

        double height = toastGui.getMinecraft().getMainWindow().getScaledHeight();
        double scale = toastGui.getMinecraft().getMainWindow().getGuiScaleFactor();
        float[] trans = new float[16];
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, trans);
        float xpos = trans[12];

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) ((xpos + 29) * scale), (int) ((height - 196) * scale), (int) (126 * scale), (int) (195 * scale));

        if (shadow)
        {
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(matrixStack, itemName, x, 18, ColorUtils.toDecimal(255, 255, 255));
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.drawString(matrixStack, itemName, x, 18, ColorUtils.toDecimal(255, 255, 255));
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}