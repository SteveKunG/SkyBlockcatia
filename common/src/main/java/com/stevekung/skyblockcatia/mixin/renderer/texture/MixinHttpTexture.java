package com.stevekung.skyblockcatia.mixin.renderer.texture;

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
    private static void doNotchTransparencyHack(NativeImage image, int x, int y, int width, int height) {}

    @Shadow
    private static void setNoAlpha(NativeImage image, int x, int y, int width, int height) {}

    @Overwrite
    private static NativeImage processLegacySkin(NativeImage nativeImage)
    {
        boolean flag = nativeImage.getHeight() == 32;

        if (flag)
        {
            NativeImage nativeimage = new NativeImage(64, 64, true);
            nativeimage.copyFrom(nativeImage);
            nativeImage.close();
            nativeimage.fillRect(0, 32, 64, 32, 0);
            nativeimage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            nativeimage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            nativeimage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            nativeimage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            nativeimage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            nativeimage.copyRect(52, 20, -8, 32, 4, 12, true, false);
            setNoAlpha(nativeimage, 0, 0, 32, 16);
            doNotchTransparencyHack(nativeimage, 32, 0, 64, 32);
            setNoAlpha(nativeimage, 0, 16, 64, 32);
            setNoAlpha(nativeimage, 16, 48, 48, 64);
            return nativeimage;
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
}