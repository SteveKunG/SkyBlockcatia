package com.stevekung.skyblockcatia.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.ITabComplete;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3APacketTabComplete;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin
{
    @Shadow
    private Minecraft gameController;

    @Inject(method = "handleTabComplete(Lnet/minecraft/network/play/server/S3APacketTabComplete;)V", at = @At("RETURN"))
    private void handleTabComplete(S3APacketTabComplete packet, CallbackInfo info)
    {
        if (this.gameController.currentScreen instanceof ITabComplete)
        {
            ITabComplete chest = (ITabComplete)this.gameController.currentScreen;
            chest.onAutocompleteResponse(packet.func_149630_c());
        }
    }
}