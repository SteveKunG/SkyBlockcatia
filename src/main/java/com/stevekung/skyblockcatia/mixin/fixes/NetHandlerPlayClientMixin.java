package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.hud.InfoUtils;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin
{
    @Inject(method = "handleUpdateTileEntity", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/tileentity/TileEntity.onDataPacket(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/network/play/server/S35PacketUpdateTileEntity;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void handleUpdateTileEntity(S35PacketUpdateTileEntity packet, CallbackInfo info, TileEntity tileentity, int i)
    {
        if (SkyBlockcatiaConfig.disableErrorLog && InfoUtils.INSTANCE.isHypixel() && tileentity == null)
        {
            info.cancel();
        }
    }
}