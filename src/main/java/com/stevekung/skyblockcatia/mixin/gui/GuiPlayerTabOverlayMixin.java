package com.stevekung.skyblockcatia.mixin.gui;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.utils.IViewerLoader;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin
{
    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/NetHandlerPlayClient.getPlayerInfoMap()Ljava/util/Collection;"))
    private Collection<NetworkPlayerInfo> filterPlayerInfo(NetHandlerPlayClient nethandlerplayclient)
    {
        return nethandlerplayclient.getPlayerInfoMap().stream().filter(network -> !((IViewerLoader)network).isLoadedFromViewer()).collect(Collectors.toList());
    }
}