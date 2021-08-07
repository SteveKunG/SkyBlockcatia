package com.stevekung.skyblockcatia.mixin.gui;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.GameProfileUtils;
import com.stevekung.skyblockcatia.utils.IViewerLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin
{
    int pingWidth;

    @Shadow
    @Final
    Minecraft mc;

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.getStringWidth(Ljava/lang/String;)I", ordinal = 0))
    private int addPingWidth(FontRenderer font, String text)
    {
        return this.mc.fontRendererObj.getStringWidth(text) + this.pingWidth;
    }

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiPlayerTabOverlay.getPlayerName(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;", ordinal = 0))
    private String getPingPlayerInfo(GuiPlayerTabOverlay overlay, NetworkPlayerInfo networkPlayerInfoIn)
    {
        if (SkyBlockcatiaSettings.INSTANCE.displayRealtimePing)
        {
            int ping = networkPlayerInfoIn.getResponseTime();
            this.pingWidth = this.mc.fontRendererObj.getStringWidth(String.valueOf(ping));
        }
        return overlay.getPlayerName(networkPlayerInfoIn);
    }

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/NetHandlerPlayClient.getPlayerInfoMap()Ljava/util/Collection;"))
    private Collection<NetworkPlayerInfo> filterPlayerInfo(NetHandlerPlayClient nethandlerplayclient)
    {
        return nethandlerplayclient.getPlayerInfoMap().stream().filter(network -> network instanceof IViewerLoader && !((IViewerLoader)network).isLoadedFromViewer()).collect(Collectors.toList());
    }

    @Inject(method = "drawPing(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V", cancellable = true, at = @At("HEAD"))
    private void drawPing(int x1, int x2, int y, NetworkPlayerInfo playerInfo, CallbackInfo info)
    {
        FontRenderer fontRenderer = this.mc.fontRendererObj;
        int ping = playerInfo.getResponseTime();

        if (SkyBlockcatiaSettings.INSTANCE.displayRealtimePing && (GameProfileUtils.getUsername().equals(playerInfo.getGameProfile().getName()) || playerInfo.getDisplayName() != null && GameProfileUtils.getUsername().equals(playerInfo.getDisplayName().getUnformattedText())))
        {
            if (ping <= 1)
            {
                ping = MainEventHandler.currentServerPing;
            }

            EnumChatFormatting color = EnumChatFormatting.GREEN;
            String pingText = String.valueOf(ping);

            if (ping >= 200 && ping < 300)
            {
                color = EnumChatFormatting.YELLOW;
            }
            else if (ping >= 300 && ping < 500)
            {
                color = EnumChatFormatting.RED;
            }
            else if (ping >= 500)
            {
                color = EnumChatFormatting.DARK_RED;
            }

            fontRenderer.drawString(color + pingText, x1 + x2 - fontRenderer.getStringWidth(pingText), y + 0.625F, 0, true);
            info.cancel();
        }
    }
}