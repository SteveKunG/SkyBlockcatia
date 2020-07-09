package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Ordering;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;

@Mixin(value = PlayerTabOverlayGui.class, priority = 1500)
public abstract class MixinPlayerTabOverlayGui
{
    @Shadow
    @Final
    @Mutable
    private static Ordering<NetworkPlayerInfo> ENTRY_ORDERING;

    @Shadow
    @Final
    @Mutable
    private Minecraft mc;

    private int playerCount;

    @Inject(method = "render(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At("HEAD"))
    private void injectPlayerCount(int width, Scoreboard scoreboard, @Nullable ScoreObjective scoreObjective, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.lobbyPlayerCount)
        {
            List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
            list = list.subList(0, Math.min(list.size(), 80));
            this.playerCount = list.size();
        }
    }

    @Redirect(method = "render(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.listFormattedStringToWidth(Ljava/lang/String;I)Ljava/util/List;", ordinal = 0))
    private List<String> addLobbyPlayerCount(FontRenderer fontRenderer, String str, int wrapWidth)
    {
        if (SBExtendedConfig.INSTANCE.lobbyPlayerCount)
        {
            List<String> origin = new CopyOnWriteArrayList<>(fontRenderer.listFormattedStringToWidth(str, wrapWidth));
            origin.add(JsonUtils.create("Lobby Players Count: ").applyTextStyle(TextFormatting.GOLD).appendSibling(JsonUtils.create(String.valueOf(this.playerCount)).applyTextStyle(TextFormatting.GREEN)).getFormattedText());
            return origin;
        }
        else
        {
            return fontRenderer.listFormattedStringToWidth(str, wrapWidth);
        }
    }
}