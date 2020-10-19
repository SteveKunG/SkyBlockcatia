package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class SBRenderUtils
{
    private static final ResourceLocation RARITY = new ResourceLocation("skyblockcatia:textures/gui/rarity.png");
    private static final Pattern PATTERN = Pattern.compile("(?<color>\\u00a7[0-9a-fk-or]).+");
    private static final Pattern PET_PATTERN = Pattern.compile("\\u00a77\\[Lvl \\d+\\] (?<color>\\u00a7[0-9a-fk-or]).+");

    // Well, this is the best way to render rarity in 1.16 because TextFormatting on this version is sucks -.-
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
                            try
                            {
                                IFormattableTextComponent lore = TextComponentUtils.fromJson(list.getString(list.size() - 1));

                                if (lore.getString().contains("COSMETIC")) // temp
                                {
                                    Style color = null;

                                    for (ITextComponent component2 : lore.getSiblings())
                                    {
                                        for (ITextComponent component3 : component2.getSiblings())
                                        {
                                            color = component3.getStyle();
                                        }
                                    }
                                    SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(TextFormatting.getValueByName(color.getColor().toString()).toString()));
                                    break;
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    return;
                }

                try
                {
                    for (ITextComponent component2 : itemStack.getDisplayName().getSiblings())
                    {
                        for (ITextComponent component3 : component2.getSiblings())
                        {
                            SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(TextFormatting.getValueByName(component3.getStyle().getColor().toString()).toString()));
                            break;
                        }
                    }
                    SBRenderUtils.renderPetRarity(displayName, itemStack, matrixStack, xPos, yPos);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
                try
                {
                    SBRenderUtils.renderPetRarity(displayName, itemStack, matrixStack, xPos, yPos);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

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

    private static void renderPetRarity(String displayName, ItemStack itemStack, MatrixStack matrixStack, int xPos, int yPos)
    {
        if (displayName.startsWith("[Lvl "))
        {
            for (ITextComponent component2 : itemStack.getDisplayName().getSiblings())
            {
                for (int i = 0; i < component2.getSiblings().size(); i++)
                {
                    if (i == 1)
                    {
                        ITextComponent color = component2.getSiblings().get(i);
                        SBRenderUtils.renderRarity(matrixStack, xPos, yPos, SBRarity.byBaseColor(TextFormatting.getValueByName(color.getStyle().getColor().toString()).toString()));
                        break;
                    }
                }
                break;
            }
        }
    }
}