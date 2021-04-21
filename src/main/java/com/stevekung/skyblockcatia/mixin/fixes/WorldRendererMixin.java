package com.stevekung.skyblockcatia.mixin.fixes;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.client.renderer.WorldRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
    @Redirect(method = "growBuffer", at = @At(value = "INVOKE", target = "org/apache/logging/log4j/Logger.warn(Ljava/lang/String;)V", remap = false))
    private void disableLog(Logger logger, String message)
    {
        if (!SkyBlockcatiaConfig.disableErrorLog)
        {
            logger.warn(message);
        }
    }
}