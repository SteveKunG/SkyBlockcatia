package com.stevekung.skyblockcatia.mixin.gui.components;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.stevekung.skyblockcatia.utils.IViewerLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(PlayerTabOverlay.class)
public class MixinPlayerTabOverlay
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/ClientPacketListener.getOnlinePlayers()Ljava/util/Collection;"))
    private Collection<PlayerInfo> filterPlayerInfo(ClientPacketListener clientplaynethandler)
    {
        return clientplaynethandler.getOnlinePlayers().stream().filter(network -> !((IViewerLoader)network).isLoadedFromViewer()).collect(Collectors.toList());
    }
}