package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Collection;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.social.SocialInteractionsScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;

@Mixin(SocialInteractionsScreen.class)
public class MixinSocialInteractionsScreen
{
    @Redirect(method = "func_244680_a(Lnet/minecraft/client/Minecraft;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/play/ClientPlayNetHandler.getPlayerInfoMap()Ljava/util/Collection;"))
    private Collection<NetworkPlayerInfo> filterPlayer(ClientPlayNetHandler handler)
    {
        return handler.getPlayerInfoMap().stream().filter(info -> !info.getGameProfile().getName().startsWith("!")).collect(Collectors.toList());
    }
}