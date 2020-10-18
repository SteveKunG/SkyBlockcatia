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

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;

@Mixin(value = PlayerTabOverlayGui.class, priority = 1500)
public abstract class MixinPlayerTabOverlayGui
{
    @Shadow
    @Final
    private static Ordering<NetworkPlayerInfo> ENTRY_ORDERING;

    @Shadow
    @Final
    private Minecraft mc;

    private int playerCount;

    @Inject(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At("HEAD"))
    private void injectPlayerCount(MatrixStack matrixStack, int width, Scoreboard scoreboard, @Nullable ScoreObjective scoreObjective, CallbackInfo info)
    {
        if (SBExtendedConfig.INSTANCE.lobbyPlayerCount)
        {
            List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
            list = list.subList(0, Math.min(list.size(), 80));
            this.playerCount = list.size();
        }
    }

    @Redirect(method = "func_238523_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.trimStringToWidth(Lnet/minecraft/util/text/ITextProperties;I)Ljava/util/List;", ordinal = 0))
    private List<IReorderingProcessor> addLobbyPlayerCount(FontRenderer fontRenderer, ITextProperties str, int wrapWidth)
    {
        if (SBExtendedConfig.INSTANCE.lobbyPlayerCount)
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
}