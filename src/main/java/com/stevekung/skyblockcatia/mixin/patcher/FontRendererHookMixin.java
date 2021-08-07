package com.stevekung.skyblockcatia.mixin.patcher;

import java.util.Deque;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import club.sk1er.patcher.hooks.FontRendererHook;
import club.sk1er.patcher.util.enhancement.hash.StringHash;
import club.sk1er.patcher.util.enhancement.text.CachedString;
import net.minecraft.client.renderer.GlStateManager;

@Mixin(value = FontRendererHook.class, remap = false)
public class FontRendererHookMixin
{
    private int state;
    private int redN;
    private int greenN;
    private int blueN;
    private static final int MARKER = 59136;

    @Inject(method = "renderStringAtPos(Ljava/lang/String;Z)Z", remap = false, at = @At(value = "INVOKE", remap = false, target = "java/lang/String.charAt(I)C", shift = Shift.AFTER, ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderColoredFont(String text, boolean shadow, CallbackInfoReturnable<Boolean> info, float posX, float posY, float red, float green, float blue, float alpha, GlStateManager.TextureState[] textureState1, GlStateManager.TextureState textureState2, StringHash hash, CachedString cachedString, int list, boolean obfuscated, CachedString value, Deque underline, Deque strikethrough, int messageChar)
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