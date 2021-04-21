package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.world.storage.MapData;

@Mixin(S34PacketMaps.class)
public class S34PacketMapsMixin
{
    @Shadow
    private byte[] mapDataBytes;

    @Inject(method = "setMapdataTo", cancellable = true, at = @At("HEAD"))
    private void disableLog(MapData data, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.disableErrorLog && (this.mapDataBytes.length == 0 || data.colors.length == 0))
        {
            info.cancel();
        }
    }
}