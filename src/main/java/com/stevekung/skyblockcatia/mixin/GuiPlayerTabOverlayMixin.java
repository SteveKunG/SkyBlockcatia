package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.config.PingMode;
import com.stevekung.skyblockcatia.config.PlayerCountMode;
import com.stevekung.skyblockcatia.event.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.SkyBlockLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class GuiPlayerTabOverlayMixin extends Gui
{
    private int playerCount;
    private int pingWidth;

    @Shadow
    @Final
    private Minecraft mc;

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.getStringWidth(Ljava/lang/String;)I", ordinal = 0))
    private int addPingWidth(FontRenderer font, String text)
    {
        return this.mc.fontRendererObj.getStringWidth(text) + this.pingWidth;
    }

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiPlayerTabOverlay.getPlayerName(Lnet/minecraft/client/network/NetworkPlayerInfo;)Ljava/lang/String;", remap = false, ordinal = 0))
    private String getPingPlayerInfo(GuiPlayerTabOverlay overlay, NetworkPlayerInfo networkPlayerInfoIn)
    {
        boolean pingDelay = PingMode.getById(ExtendedConfig.instance.pingMode).equalsIgnoreCase("ping_and_delay");
        int ping = networkPlayerInfoIn.getResponseTime();
        String pingText = String.valueOf(ping);

        if (pingDelay)
        {
            pingText = pingText + "/" + String.format("%.2f", ping / 1000.0F) + "s";
            this.mc.fontRendererObj.setUnicodeFlag(true);
        }

        this.pingWidth = ConfigManagerIN.enableCustomPlayerList ? this.mc.fontRendererObj.getStringWidth(pingText) : 0;

        if (pingDelay)
        {
            this.mc.fontRendererObj.setUnicodeFlag(false);
        }
        return overlay.getPlayerName(networkPlayerInfoIn);
    }

    @Inject(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At("HEAD"))
    private void injectPlayerCount(int width, Scoreboard scoreboard, @Nullable ScoreObjective scoreObjective, CallbackInfo info)
    {
        if (this.isPlayerCountEnabled())
        {
            List<NetworkPlayerInfo> list = GuiPlayerTabOverlay.field_175252_a.sortedCopy(this.mc.thePlayer.sendQueue.getPlayerInfoMap());
            this.playerCount = HUDRenderEventHandler.getPlayerCount(list);
        }
    }

    @Redirect(method = "renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.listFormattedStringToWidth(Ljava/lang/String;I)Ljava/util/List;", ordinal = 0))
    private List<String> addLobbyPlayerCount(FontRenderer fontRenderer, String str, int wrapWidth)
    {
        if (this.isPlayerCountEnabled())
        {
            List<String> origin = new CopyOnWriteArrayList<>(fontRenderer.listFormattedStringToWidth(str, wrapWidth));
            origin.add(JsonUtils.create("Lobby Players Count: ").setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(String.valueOf(this.playerCount)).setChatStyle(JsonUtils.green())).getFormattedText());
            return origin;
        }
        else
        {
            return fontRenderer.listFormattedStringToWidth(str, wrapWidth);
        }
    }

    @Inject(method = "drawPing(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V", cancellable = true, at = @At("HEAD"))
    private void drawPing(int x1, int x2, int y, NetworkPlayerInfo playerInfo, CallbackInfo info)
    {
        boolean pingDelay = PingMode.getById(ExtendedConfig.instance.pingMode).equalsIgnoreCase("ping_and_delay");
        FontRenderer fontRenderer = this.mc.fontRendererObj;
        int ping = playerInfo.getResponseTime();

        if (ConfigManagerIN.enableCustomPlayerList)
        {
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

            if (pingDelay)
            {
                pingText = String.valueOf(ping) + "/" + String.format("%.2f", (float)ping / 1000) + "s";
                fontRenderer.setUnicodeFlag(true);
            }

            fontRenderer.drawString(color + pingText, x1 + x2 - fontRenderer.getStringWidth(pingText), y + 0.625F, 0, true);

            if (pingDelay)
            {
                fontRenderer.setUnicodeFlag(false);
            }
            info.cancel();
        }
    }

    private boolean isPlayerCountEnabled()
    {
        return ExtendedConfig.instance.lobbyPlayerCount && PlayerCountMode.getById(ExtendedConfig.instance.playerCountMode).equalsIgnoreCase("tab_list") && HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION != SkyBlockLocation.YOUR_ISLAND;
    }
}