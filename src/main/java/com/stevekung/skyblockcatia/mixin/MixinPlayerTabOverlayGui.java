package com.stevekung.skyblockcatia.mixin;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.IViewerLoader;
import com.stevekung.skyblockcatia.utils.PlayerCountMode;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;

@Mixin(value = PlayerTabOverlayGui.class, priority = 1500)
public abstract class MixinPlayerTabOverlayGui
{
    private int playerCount;
//    private int pingWidth;
    
    @Shadow
    @Final
    private Minecraft mc;
    
//    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.getStringPropertyWidth(Lnet/minecraft/util/text/ITextProperties;)I"))
//    private int addPingWidth(FontRenderer font, ITextProperties properties)
//    {
//        return this.mc.fontRenderer.getStringPropertyWidth(properties) + this.pingWidth;
//    }
    
    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/play/ClientPlayNetHandler.getPlayerInfoMap()Ljava/util/Collection;"))
    private Collection<NetworkPlayerInfo> filterPlayerInfo(ClientPlayNetHandler clientplaynethandler)
    {
        return clientplaynethandler.getPlayerInfoMap().stream().filter(network -> !((IViewerLoader)network).isLoadedFromViewer()).collect(Collectors.toList());
    }
    
//    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/overlay/PlayerTabOverlayGui.getDisplayName(Lnet/minecraft/client/network/play/NetworkPlayerInfo;)Lnet/minecraft/util/text/ITextComponent;", ordinal = 0))
//    private String getPingPlayerInfo(PlayerTabOverlayGui overlay, NetworkPlayerInfo networkPlayerInfoIn)
//    {
//        boolean pingDelay = PingMode.getById(ExtendedConfig.instance.pingMode).equalsIgnoreCase("ping_and_delay");
//        int ping = networkPlayerInfoIn.getResponseTime();
//        String pingText = String.valueOf(ping);
//
//        if (pingDelay)
//        {
//            pingText = pingText + "/" + String.format("%.2f", ping / 1000.0F) + "s";
//            this.mc.fontRenderer.setUnicodeFlag(true);
//        }
//
//        this.pingWidth = ConfigManagerIN.enableCustomPlayerList ? this.mc.fontRenderer.getStringWidth(pingText) : 0;
//
//        if (pingDelay)
//        {
//            this.mc.fontRenderer.setUnicodeFlag(false);
//        }
//        return overlay.getPlayerName(networkPlayerInfoIn);
//    }

    @Inject(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At("HEAD"))
    private void injectPlayerCount(MatrixStack matrixStack, int width, Scoreboard scoreboard, @Nullable ScoreObjective scoreObjective, CallbackInfo info)
    {
        if (this.isPlayerCountEnabled())
        {
            List<NetworkPlayerInfo> list = PlayerTabOverlayGui.ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
            this.playerCount = HUDRenderEventHandler.getPlayerCount(list);
        }
    }

    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.trimStringToWidth(Lnet/minecraft/util/text/ITextProperties;I)Ljava/util/List;", ordinal = 0))
    private List<IReorderingProcessor> addLobbyPlayerCount(FontRenderer fontRenderer, ITextProperties str, int wrapWidth)
    {
        if (this.isPlayerCountEnabled())
        {
            List<IReorderingProcessor> origin = new CopyOnWriteArrayList<>(fontRenderer.trimStringToWidth(str, wrapWidth));
            origin.add(TextComponentUtils.formatted("Lobby Players Count: ", TextFormatting.GOLD).append(TextComponentUtils.formatted(String.valueOf(this.playerCount), TextFormatting.GREEN)).func_241878_f());
            return origin;
        }
        else
        {
            return fontRenderer.trimStringToWidth(str, wrapWidth);
        }
    }
    
    private boolean isPlayerCountEnabled()
    {
        return SBExtendedConfig.INSTANCE.lobbyPlayerCount && SBExtendedConfig.INSTANCE.playerCountMode == PlayerCountMode.TAB_LIST && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION != SBLocation.YOUR_ISLAND;
    }
}