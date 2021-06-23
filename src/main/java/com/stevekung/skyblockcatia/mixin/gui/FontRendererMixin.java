package com.stevekung.skyblockcatia.mixin.gui;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.utils.SupporterUtils;

import net.minecraft.client.gui.FontRenderer;

@Mixin(value = FontRenderer.class, priority = 100)
public abstract class FontRendererMixin
{
    @Unique
    private static final int MARKER = 59136;

    @Unique
    private boolean dropShadow;

    @Unique
    private int state;

    @Unique
    private int redN;

    @Unique
    private int greenN;

    @Unique
    private int blueN;

    //////////////////////

    @Shadow
    private float alpha;

    @Shadow
    protected float posX;

    @Shadow
    protected float posY;

    @Shadow
    protected byte[] glyphWidth;

    @Shadow
    protected abstract void setColor(float r, float g, float b, float a);

    @Shadow
    protected abstract void loadGlyphTexture(int page);

    @Shadow
    private static boolean isFormatColor(char colorChar)
    {
        return false;
    }

    @Shadow
    private static boolean isFormatSpecial(char formatChar)
    {
        return false;
    }

    @Inject(method = "renderString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"))
    private void renderString(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info)
    {
        this.dropShadow = dropShadow;
    }

    @ModifyVariable(method = "renderString(Ljava/lang/String;FFIZ)I", at = @At(value = "FIELD", target = "net/minecraft/client/gui/FontRenderer.bidiFlag:Z"), argsOnly = true)
    private String renderString(String text)
    {
        return SupporterUtils.getSpecialColor(text);
    }

    @Inject(method = "renderDefaultChar(IZ)F", cancellable = true, at = @At("HEAD"))
    private void renderDefaultChar(int ch, boolean italic, CallbackInfoReturnable<Float> info)
    {
        if (ch >= MARKER && ch <= MARKER + 255)
        {
            int value = ch & 255;

            switch (this.state)
            {
                case 0:
                    this.redN = value;
                    break;
                case 1:
                    this.greenN = value;
                    break;
                case 2:
                    this.blueN = value;
                    break;
                default:
                    this.setColor(1.0F, 1.0F, 1.0F, this.alpha);
                    info.setReturnValue(0.0F);
            }

            this.state = ++this.state % 3;
            int color = this.redN << 16 | this.greenN << 8 | this.blueN;

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }
            if (this.dropShadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }
            this.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color >> 0 & 255) / 255.0F, this.alpha);
            info.setReturnValue(0.0F);
        }
        if (this.state != 0)
        {
            this.state = 0;
            this.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        }
    }

    @Overwrite
    protected float renderUnicodeChar(char ch, boolean italic)
    {
        if (ch >= MARKER && ch <= MARKER + 255)
        {
            int value = ch & 255;

            switch (this.state)
            {
                case 0:
                    this.redN = value;
                    break;
                case 1:
                    this.greenN = value;
                    break;
                case 2:
                    this.blueN = value;
                    break;
                default:
                    this.setColor(1.0F, 1.0F, 1.0F, this.alpha);
                    return 0.0F;
            }

            this.state = ++this.state % 3;
            int color = this.redN << 16 | this.greenN << 8 | this.blueN;

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }
            if (this.dropShadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }
            this.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color >> 0 & 255) / 255.0F, this.alpha);
            return 0.0F;
        }
        if (this.state != 0)
        {
            this.state = 0;
            this.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        }

        if (this.glyphWidth[ch] == 0)
        {
            return 0.0F;
        }
        else
        {
            int i = ch / 256;
            this.loadGlyphTexture(i);
            int j = this.glyphWidth[ch] >>> 4;
            int k = this.glyphWidth[ch] & 15;
            float f = j;
            float f1 = k + 1;
            float f2 = ch % 16 * 16 + f;
            float f3 = (ch & 255) / 16 * 16;
            float f4 = f1 - f - 0.02F;
            float f5 = italic ? 1.0F : 0.0F;
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
            GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
            GL11.glEnd();
            return (f1 - f) / 2.0F + 1.0F;
        }
    }

    @Overwrite
    public static String getFormatFromString(String text)
    {
        String s = "";
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = text.charAt(i + 1);

                if (isFormatColor(c0))
                {
                    s = "\u00a7" + c0;
                }
                else if (isFormatSpecial(c0))
                {
                    s = s + "\u00a7" + c0;
                }
                else if (c0 >= MARKER && c0 <= MARKER + 255)
                {
                    s = String.format("%s%s%s", c0, text.charAt(i + 1), text.charAt(i + 2));
                    i += 2;
                }
            }
        }
        return s;
    }
}