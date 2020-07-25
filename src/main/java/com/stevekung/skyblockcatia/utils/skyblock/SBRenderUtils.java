package com.stevekung.skyblockcatia.utils.skyblock;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.ColorUtils.RGB;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class SBRenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");

    public static ItemStack getSkullItemStack(String skullId, String skullValue)
    {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
        CompoundNBT compound = new CompoundNBT();
        CompoundNBT properties = new CompoundNBT();
        properties.putString("Id", skullId);
        CompoundNBT texture = new CompoundNBT();
        ListNBT list = new ListNBT();
        CompoundNBT value = new CompoundNBT();
        value.putString("Value", skullValue);
        list.add(value);
        texture.put("textures", list);
        properties.put("Properties", texture);
        compound.put("SkullOwner", properties);
        itemStack.setTag(compound);
        return itemStack;
    }

    public static void renderRarity(ItemStack itemStack, int xPos, int yPos)
    {
        if (!itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundNBT compound = itemStack.getTag().getCompound("display");

            if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
            {
                ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                for (int j1 = 0; j1 < list.size(); ++j1)
                {
                    String lore = ITextComponent.Serializer.fromJson(list.getString(j1)).getFormattedText();
                    RGB common = ColorUtils.stringToRGB("255,255,255");
                    RGB uncommon = ColorUtils.stringToRGB("85,255,85");
                    RGB rare = ColorUtils.stringToRGB("85,85,255");
                    RGB epic = ColorUtils.stringToRGB("170,0,170");
                    RGB legendary = ColorUtils.stringToRGB("255,170,0");
                    RGB mythic = ColorUtils.stringToRGB("255,85,255");
                    RGB special = ColorUtils.stringToRGB("255,85,85");
                    RGB verySpecial = ColorUtils.stringToRGB("170,0,0");

                    if (SBRenderUtils.checkRarityString(lore, TextFormatting.WHITE, "COMMON"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, common);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.GREEN, "UNCOMMON"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, uncommon);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.BLUE, "RARE"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, rare);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.DARK_PURPLE, "EPIC"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, epic);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.GOLD, "LEGENDARY"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, legendary);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.LIGHT_PURPLE, "MYTHIC"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, mythic);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.RED, "SPECIAL"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, special);
                    }
                    else if (SBRenderUtils.checkRarityString(lore, TextFormatting.RED, "VERY SPECIAL"))
                    {
                        SBRenderUtils.renderRarity(xPos, yPos, verySpecial);
                    }
                }
            }
        }
    }

    public static void renderRarity(int xPos, int yPos, RGB color)
    {
        float alpha = SBExtendedConfig.INSTANCE.itemRarityOpacity / 100.0F;
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderUtils.bindTexture(RARITY);
        RenderSystem.color4f(color.floatRed(), color.floatGreen(), color.floatBlue(), alpha);
        RenderSystem.blendFunc(770, 771);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        AbstractGui.blit(xPos, yPos, 0, 0, 16, 16, 16, 16);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.disableAlphaTest();
    }

    // Credit to https://gist.github.com/killjoy1221/71b4cd975b92afe8dbd2e5f6222b1140
    public static void drawLongItemName(ToastGui toastGui, long delta, long firstDrawTime, String itemName, long minDraw, long maxDraw, long timeUntilGone, long textSpeed, boolean shadow)
    {
        int x = 30;
        int textWidth = toastGui.getMinecraft().fontRenderer.getStringWidth(itemName);
        int maxSize = textWidth - 135;
        long timeElapsed = delta - firstDrawTime - minDraw;
        long timeElapsed2 = maxDraw - delta - timeUntilGone;
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
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(itemName, x, 18, ColorUtils.rgbToDecimal(255, 255, 255));
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.drawString(itemName, x, 18, ColorUtils.rgbToDecimal(255, 255, 255));
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private static boolean checkRarityString(String lore, TextFormatting color, String text)
    {
        return lore.startsWith(color + "" + TextFormatting.BOLD + text) || lore.startsWith(color.toString() + TextFormatting.BOLD + TextFormatting.OBFUSCATED + "a" + TextFormatting.RESET + TextFormatting.GRAY + " " + TextFormatting.RESET + color + TextFormatting.BOLD + text);
    }
}