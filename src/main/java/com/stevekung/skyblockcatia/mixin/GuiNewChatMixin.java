package com.stevekung.skyblockcatia.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.*;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

@Mixin(value = GuiNewChat.class, priority = 500)
public abstract class GuiNewChatMixin extends Gui
{
    @Shadow
    @Final
    @Mutable
    private Minecraft mc;

    @Shadow
    @Final
    @Mutable
    private List<ChatLine> field_146253_i;

    @Shadow
    private int scrollPos;

    @Shadow
    private boolean isScrolled;

    @Shadow
    protected abstract int getLineCount();

    @Shadow
    protected abstract boolean getChatOpen();

    @Shadow
    protected abstract float getChatScale();

    @Shadow
    protected abstract int getChatWidth();

    @Overwrite
    public void drawChat(int updateCounter)
    {
        GlStateManager.disableDepth();

        if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            int i = this.getLineCount();
            int j = this.field_146253_i.size();
            float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            if (j > 0)
            {
                boolean flag = false;

                if (this.getChatOpen())
                {
                    flag = true;
                }

                float f1 = this.getChatScale();
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 8.0F, 0.0F);
                GlStateManager.scale(f1, f1, 1.0F);
                int l = 0;

                for (int i1 = 0; i1 + this.scrollPos < this.field_146253_i.size() && i1 < i; ++i1)
                {
                    ChatLine chatline = this.field_146253_i.get(i1 + this.scrollPos);

                    if (chatline != null)
                    {
                        int j1 = updateCounter - chatline.getUpdatedCounter();

                        if (j1 < 200 || flag)
                        {
                            double d0 = j1 / 200.0D;
                            d0 = 1.0D - d0;
                            d0 = d0 * 10.0D;
                            d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
                            d0 = d0 * d0;
                            int l1 = (int)(255.0D * d0);

                            if (flag)
                            {
                                l1 = 255;
                            }

                            l1 = (int)(l1 * f);
                            ++l;

                            if (l1 > 3)
                            {
                                int j2 = -i1 * 9;
                                int k = MathHelper.ceiling_float_int(this.getChatWidth() / f1);
                                String text = chatline.getChatComponent().getFormattedText();

                                if (!ConfigManagerIN.enableFastChatRender)
                                {
                                    Gui.drawRect(-2, j2 - 9, 0 + k + 4, j2, l1 / 2 << 24);
                                }

                                GlStateManager.enableBlend();
                                this.mc.fontRendererObj.drawStringWithShadow(text, 0.0F, j2 - 8, 16777215 + (l1 << 24));
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                }
                if (flag)
                {
                    int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = j * k2 + j;
                    int i3 = l * k2 + l;
                    int j3 = this.scrollPos * i3 / j;
                    int k1 = i3 * i3 / l2;

                    if (l2 != i3)
                    {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = this.isScrolled ? 13382451 : 3355562;
                        Gui.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        Gui.drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                    }
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.enableDepth();
    }

    @Overwrite
    public IChatComponent getChatComponent(int mouseX, int mouseY)
    {
        if (!this.getChatOpen())
        {
            return null;
        }
        else
        {
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 40;
            j = MathHelper.floor_float(j / f);
            k = MathHelper.floor_float(k / f);

            if (j >= 0 && k >= 0)
            {
                int l = Math.min(this.getLineCount(), this.field_146253_i.size());

                if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l)
                {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.field_146253_i.size())
                    {
                        ChatLine chatline = this.field_146253_i.get(i1);
                        int j1 = 0;

                        for (IChatComponent component : chatline.getChatComponent())
                        {
                            if (component instanceof ChatComponentText)
                            {
                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)component).getChatComponentText_TextValue(), false));

                                if (j1 > j)
                                {
                                    return component;
                                }
                            }
                        }
                    }
                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }
}