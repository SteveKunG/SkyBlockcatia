package com.stevekung.skyblockcatia.mixin.renderer.texture;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import com.mojang.blaze3d.platform.NativeImage;
import com.stevekung.skyblockcatia.utils.PlatformConfig;
import net.minecraft.client.renderer.texture.HttpTexture;

@Mixin(HttpTexture.class)
public class MixinHttpTexture
{
    @Shadow
    @Final
    static Logger LOGGER;

    @Shadow
    @Final
    String urlString;

    @Shadow
    static void doNotchTransparencyHack(NativeImage image, int x, int y, int width, int height) {}

    @Shadow
    static void setNoAlpha(NativeImage image, int x, int y, int width, int height) {}

    @Overwrite
    private NativeImage processLegacySkin(NativeImage nativeImage)
    {
        var width = nativeImage.getWidth();
        var height = nativeImage.getHeight();

        if (width == 64 && (height == 32 || height == 64))
        {
            var flag = nativeImage.getHeight() == 32;

            if (flag)
            {
                NativeImage nativeImage2 = new NativeImage(64, 64, true);
                nativeImage2.copyFrom(nativeImage);
                nativeImage.close();
                nativeImage2.fillRect(0, 32, 64, 32, 0);
                nativeImage2.copyRect(4, 16, 16, 32, 4, 4, true, false);
                nativeImage2.copyRect(8, 16, 16, 32, 4, 4, true, false);
                nativeImage2.copyRect(0, 20, 24, 32, 4, 12, true, false);
                nativeImage2.copyRect(4, 20, 16, 32, 4, 12, true, false);
                nativeImage2.copyRect(8, 20, 8, 32, 4, 12, true, false);
                nativeImage2.copyRect(12, 20, 16, 32, 4, 12, true, false);
                nativeImage2.copyRect(44, 16, -8, 32, 4, 4, true, false);
                nativeImage2.copyRect(48, 16, -8, 32, 4, 4, true, false);
                nativeImage2.copyRect(40, 20, 0, 32, 4, 12, true, false);
                nativeImage2.copyRect(44, 20, -8, 32, 4, 12, true, false);
                nativeImage2.copyRect(48, 20, -16, 32, 4, 12, true, false);
                nativeImage2.copyRect(52, 20, -8, 32, 4, 12, true, false);
                setNoAlpha(nativeImage2, 0, 0, 32, 16);
                doNotchTransparencyHack(nativeImage2, 32, 0, 64, 32);
                setNoAlpha(nativeImage2, 0, 16, 64, 32);
                setNoAlpha(nativeImage2, 16, 48, 48, 64);
                return nativeImage2;
            }

            if (PlatformConfig.getSkinRenderingFix())
            {
                nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
                nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
                nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
                nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
                nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
                nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
                nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
                nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
                nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
                nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
                nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
                nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
            }
            else
            {
                setNoAlpha(nativeImage, 0, 0, 32, 16);

                if (flag)
                {
                    doNotchTransparencyHack(nativeImage, 32, 0, 64, 32);
                }

                setNoAlpha(nativeImage, 0, 16, 64, 32);
                setNoAlpha(nativeImage, 16, 48, 48, 64);
            }
            return nativeImage;
        }
        else
        {
            nativeImage.close();
            LOGGER.warn("Discarding incorrectly sized ({}x{}) skin texture from {}", width, height, this.urlString);
            return null;
        }
    }
}