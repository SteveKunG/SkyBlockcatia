package com.stevekung.skyblockcatia.mixin.patcher;

import java.util.Deque;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import club.sk1er.patcher.hooks.FontRendererHook;
import club.sk1er.patcher.util.enhancement.text.CachedString;
import club.sk1er.patcher.util.hash.StringHash;
import net.minecraft.client.renderer.GlStateManager;

@Mixin(value = FontRendererHook.class, remap = false)
public abstract class FontRendererHookMixin
{
    private int state;
    private int redN;
    private int greenN;
    private int blueN;
    private static final int MARKER = 59136;

    @Inject(method = "renderStringAtPos(Ljava/lang/String;Z)Z", remap = false, at = @At(value = "INVOKE", remap = false, target = "java/lang/String.charAt(I)C", shift = Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderStringAtPos(String text, boolean shadow, CallbackInfoReturnable info,
            int list, float posX, float posY, float red, float green, float blue, float alpha, StringHash hash, GlStateManager.TextureState textureStates[], int activeTextureUnit, GlStateManager.TextureState textureState, boolean cacheFontData, CachedString cachedString, boolean obfuscated, CachedString value, int colorCode[], Deque underline, Deque strikeThough, int messageChar)
    {
        char letter = text.charAt(messageChar);

        if (letter >= MARKER && letter <= MARKER + 255)
        {
            int value1 = letter & 255;

            switch (this.state)
            {
            case 0:
                this.redN = value1;
                break;
            case 1:
                this.greenN = value1;
                break;
            case 2:
                this.blueN = value1;
                break;
            default:
                break;
            }

            this.state = ++this.state % 3;
            int color = this.redN << 16 | this.greenN << 8 | this.blueN;

            if ((color & -67108864) == 0)
            {
                color |= -16777216;
            }
            if (shadow)
            {
                color = (color & 16579836) >> 2 | color & -16777216;
            }

            float colorRed = (color >> 16 & 255) / 255.0F;
            float colorGreen = (color >> 8 & 255) / 255.0F;
            float colorBlue = (color >> 0 & 255) / 255.0F;
            GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);
            value.setLastAlpha(alpha);
            value.setLastGreen(colorGreen);
            value.setLastBlue(colorBlue);
            value.setLastRed(colorRed);
        }
    }
}