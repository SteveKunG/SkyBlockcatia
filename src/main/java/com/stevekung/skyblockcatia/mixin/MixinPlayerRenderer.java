package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerModelPart;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer
{
    @Redirect(method = "setModelVisibilities(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/player/AbstractClientPlayerEntity.isWearing(Lnet/minecraft/entity/player/PlayerModelPart;)Z"))
    private boolean fixSecondLayer(AbstractClientPlayerEntity clientPlayer, PlayerModelPart part)
    {
        return SkyBlockAPIViewerScreen.renderSecondLayer || clientPlayer.isWearing(part);
    }
}