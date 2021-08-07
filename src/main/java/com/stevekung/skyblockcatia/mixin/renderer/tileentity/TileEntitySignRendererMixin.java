package com.stevekung.skyblockcatia.mixin.renderer.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.utils.IModifiedSign;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

@Mixin(TileEntitySignRenderer.class)
public abstract class TileEntitySignRendererMixin extends TileEntitySpecialRenderer<TileEntitySign>
{
    @Shadow
    @Final
    static ResourceLocation SIGN_TEXTURE;

    @Shadow
    @Final
    ModelSign model;

    @Inject(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySign;DDDFI)V", cancellable = true, at = @At("HEAD"))
    private void renderTileEntityAt(TileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enableOverwriteSignEditing)
        {
            Block block = te.getBlockType();
            GlStateManager.pushMatrix();
            float f = 0.6666667F;

            if (block == Blocks.standing_sign)
            {
                GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
                float f1 = te.getBlockMetadata() * 360 / 16.0F;
                GlStateManager.rotate(-f1, 0.0F, 1.0F, 0.0F);
                this.model.signStick.showModel = true;
            }
            else
            {
                int k = te.getBlockMetadata();
                float f2 = 0.0F;

                if (k == 2)
                {
                    f2 = 180.0F;
                }

                if (k == 4)
                {
                    f2 = 90.0F;
                }

                if (k == 5)
                {
                    f2 = -90.0F;
                }

                GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * f, (float)z + 0.5F);
                GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
                this.model.signStick.showModel = false;
            }

            if (destroyStage >= 0)
            {
                this.bindTexture(DESTROY_STAGES[destroyStage]);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 2.0F, 1.0F);
                GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                GlStateManager.matrixMode(5888);
            }
            else
            {
                this.bindTexture(SIGN_TEXTURE);
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.scale(f, -f, -f);
            this.model.renderSign();
            GlStateManager.popMatrix();
            FontRenderer font = this.getFontRenderer();
            float f3 = 0.015625F * f;
            GlStateManager.translate(0.0F, 0.5F * f, 0.07F * f);
            GlStateManager.scale(f3, -f3, f3);
            GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
            GlStateManager.depthMask(false);

            if (destroyStage < 0)
            {
                for (int i = 0; i < te.signText.length; ++i)
                {
                    if (te.signText[i] != null)
                    {
                        IChatComponent ichatcomponent = te.signText[i];
                        List<IChatComponent> list = this.splitText(ichatcomponent, 90, font, false, true);
                        String text = list != null && list.size() > 0 ? list.get(0).getUnformattedText() : "";

                        font.drawString(text, -font.getStringWidth(text) / 2, i * 10 - te.signText.length * 5, 0, false);

                        if (i == te.lineBeingEdited && ((IModifiedSign)te).getSelectionStart() >= 0)
                        {
                            int k = font.getStringWidth(text.substring(0, Math.max(Math.min(((IModifiedSign)te).getSelectionStart(), text.length()), 0)));
                            int l = font.getBidiFlag() ? -1 : 1;
                            int i1 = (k - font.getStringWidth(text) / 2) * l;
                            int j1 = i * 10 - te.signText.length * 5;

                            if (((IModifiedSign)te).getCaretVisible())
                            {
                                if (((IModifiedSign)te).getSelectionStart() < text.length())
                                {
                                    Gui.drawRect(i1, j1 - 1, i1 + 1, j1 + 9, -16777216);
                                }
                                else
                                {
                                    font.drawString("_", i1, j1, 0, false);
                                }
                            }

                            if (((IModifiedSign)te).getSelectionEnd() != ((IModifiedSign)te).getSelectionStart())
                            {
                                int k1 = Math.min(((IModifiedSign)te).getSelectionStart(), ((IModifiedSign)te).getSelectionEnd());
                                int l1 = Math.max(((IModifiedSign)te).getSelectionStart(), ((IModifiedSign)te).getSelectionEnd());
                                int i2 = (font.getStringWidth(text.substring(0, k1)) - font.getStringWidth(text) / 2) * l;
                                int j2 = (font.getStringWidth(text.substring(0, l1)) - font.getStringWidth(text) / 2) * l;
                                this.invertSelection(Math.min(i2, j2), j1, Math.max(i2, j2), j1 + 9);
                            }
                        }
                    }
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();

            if (destroyStage >= 0)
            {
                GlStateManager.matrixMode(5890);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
            }
            info.cancel();
        }
    }

    private void invertSelection(int left, int top, int right, int bottom)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, top, 0.0D).endVertex();
        worldRenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    private List<IChatComponent> splitText(IChatComponent component, int maxTextLength, FontRenderer font, boolean trimSpace, boolean forceTextColor)
    {
        int length = 0;
        IChatComponent component1 = new ChatComponentText("");
        List<IChatComponent> list = new ArrayList<>();
        List<IChatComponent> list1 = Lists.newArrayList(component);

        for (int i = 0; i < list1.size(); ++i)
        {
            IChatComponent component2 = list1.get(i);
            String text = component2.getUnformattedTextForChat();
            boolean flag = false;

            if (text.contains("\n"))
            {
                int k = text.indexOf(10);
                String s1 = text.substring(k + 1);
                text = text.substring(0, k + 1);
                list1.add(i + 1, new ChatComponentText(s1).setChatStyle(component2.getChatStyle().createShallowCopy()));
                flag = true;
            }

            String s4 = GuiUtilRenderComponents.func_178909_a(component2.getChatStyle().getFormattingCode() + text, forceTextColor);
            String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = font.getStringWidth(s5);
            IChatComponent component3 = new ChatComponentText(s5).setChatStyle(component2.getChatStyle().createShallowCopy());

            if (length + i1 > maxTextLength)
            {
                String s2 = font.trimStringToWidth(s4, maxTextLength - length, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && !s3.isEmpty())
                {
                    int l = s3.charAt(0) != ' ' ? s2.lastIndexOf(32) : s2.length();

                    if (l >= 0 && font.getStringWidth(s4.substring(0, l)) > 0)
                    {
                        s2 = s4.substring(0, l);

                        if (trimSpace)
                        {
                            ++l;
                        }
                        s3 = s4.substring(l);
                    }
                    else if (length > 0 && !s4.contains(" "))
                    {
                        s2 = "";
                        s3 = s4;
                    }
                    s3 = this.getFormatString(s2) + s3;
                    list1.add(i + 1, new ChatComponentText(s3).setChatStyle(component2.getChatStyle().createShallowCopy()));
                }
                i1 = font.getStringWidth(s2);
                component3 = new ChatComponentText(s2);
                component3.setChatStyle(component2.getChatStyle().createShallowCopy());
                flag = true;
            }

            if (length + i1 <= maxTextLength)
            {
                length += i1;
                component1.appendSibling(component3);
            }
            else
            {
                flag = true;
            }

            if (flag)
            {
                list.add(component1);
                length = 0;
                component1 = new ChatComponentText("");
            }
        }
        list.add(component1);
        return list;
    }

    private String getFormatString(String text)
    {
        StringBuilder builder = new StringBuilder();
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                EnumChatFormatting format = this.fromFormattingCode(text.charAt(i + 1));

                if (format != null)
                {
                    if (!format.isFancyStyling())
                    {
                        builder.setLength(0);
                    }
                    if (format != EnumChatFormatting.RESET)
                    {
                        builder.append(format);
                    }
                }
            }
        }
        return builder.toString();
    }

    @Nullable
    private EnumChatFormatting fromFormattingCode(char code)
    {
        char formatCode = Character.toString(code).toLowerCase(Locale.ROOT).charAt(0);

        for (EnumChatFormatting format : EnumChatFormatting.values())
        {
            if (format.formattingCode == formatCode)
            {
                return format;
            }
        }
        return null;
    }
}