package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    @Redirect(method = "processLegacySkin(Lnet/minecraft/client/renderer/texture/NativeImage;)Lnet/minecraft/client/renderer/texture/NativeImage;", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/texture/DownloadingTexture.setAreaOpaque(Lnet/minecraft/client/renderer/texture/NativeImage;IIII)V", ordinal = 0))
    private static void fixSkinRendering(NativeImage image, int x, int y, int width, int height)
    {
        if (SkyBlockcatiaConfig.GENERAL.enableSkinRenderingFix.get())
        {
            MixinDownloadingTexture.setAreaTransparent(image, x, y, width, height);
        }
        else
        {
            MixinDownloadingTexture.setAreaOpaque(image, x, y, width, height);
        }
    }
}