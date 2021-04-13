package com.stevekung.skyblockcatia.mixin.gui.overlay;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.utils.IViewerLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;

@Mixin(PlayerTabOverlayGui.class)
public class MixinPlayerTabOverlayGui
{
    @Shadow
    @Final
    private Minecraft mc;

    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/play/ClientPlayNetHandler.getPlayerInfoMap()Ljava/util/Collection;"))
    private Collection<NetworkPlayerInfo> filterPlayerInfo(ClientPlayNetHandler clientplaynethandler)
    {
        return clientplaynethandler.getPlayerInfoMap().stream().filter(network -> !((IViewerLoader)network).isLoadedFromViewer()).collect(Collectors.toList());
    }
}