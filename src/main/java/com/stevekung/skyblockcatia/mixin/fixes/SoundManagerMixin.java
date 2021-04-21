package com.stevekung.skyblockcatia.mixin.fixes;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

@Mixin(SoundManager.class)
public class SoundManagerMixin
{
    @Redirect(method = "playSound", at = @At(value = "INVOKE", target = "org/apache/logging/log4j/Logger.warn(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;[Ljava/lang/Object;)V", remap = false, ordinal = 0))
    private void disableLog(Logger logger, Marker marker, String message, Object[] params, ISound sound)
    {
        if (!SkyBlockcatiaConfig.disableErrorLog && !sound.getSoundLocation().toString().equals("minecraft:"))
        {
            logger.warn(marker, message, params);
        }
    }
}