package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;

@Mixin(DownloadingTexture.class)
public abstract class MixinDownloadingTexture
{
    @Shadow
    private static void setAreaTransparent(NativeImage image, int x, int y, int width, int height)
    {
        throw new Error();
    }

    @Shadow
    private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height)
    {
        throw new Error();
    }

    @Overwrite
    private static NativeImage processLegacySkin(NativeImage nativeImage)
    {
        boolean flag = nativeImage.getHeight() == 32;

        if (flag)
        {
            NativeImage nativeimage = new NativeImage(64, 64, true);
            nativeimage.copyImageData(nativeImage);
            nativeImage.close();
            nativeImage = nativeimage;
            nativeimage.fillAreaRGBA(0, 32, 64, 32, 0);
            nativeimage.copyAreaRGBA(4, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(8, 16, 16, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(0, 20, 24, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(4, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(8, 20, 8, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(12, 20, 16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(44, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(48, 16, -8, 32, 4, 4, true, false);
            nativeimage.copyAreaRGBA(40, 20, 0, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(44, 20, -8, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(48, 20, -16, 32, 4, 12, true, false);
            nativeimage.copyAreaRGBA(52, 20, -8, 32, 4, 12, true, false);
        }

        if (SkyBlockcatiaConfig.GENERAL.enableSkinRenderingFix.get())
        {
            nativeImage.copyAreaRGBA(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyAreaRGBA(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyAreaRGBA(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyAreaRGBA(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyAreaRGBA(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.copyAreaRGBA(52, 20, -8, 32, 4, 12, true, false);
        }
        else
        {
            setAreaOpaque(nativeImage, 0, 0, 32, 16);

            if (flag)
            {
                setAreaTransparent(nativeImage, 32, 0, 64, 32);
            }

            setAreaOpaque(nativeImage, 0, 16, 64, 32);
            setAreaOpaque(nativeImage, 16, 48, 48, 64);
        }
        return nativeImage;
    }
}