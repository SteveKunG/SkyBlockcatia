package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;
import com.stevekung.skyblockcatia.renderer.entity.layers.LayerGlowingTexture;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerModelPart;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer
{
    private final PlayerRenderer that = (PlayerRenderer) (Object) this;

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererManager;Z)V", at = @At("RETURN"))
    private void init(EntityRendererManager manager, boolean useSmallArms, CallbackInfo info)
    {
        this.that.addLayer(new LayerGlowingTexture(this.that));
    }

    @Redirect(method = "setModelVisibilities(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/player/AbstractClientPlayerEntity.isWearing(Lnet/minecraft/entity/player/PlayerModelPart;)Z"))
    private boolean fixSecondLayer(AbstractClientPlayerEntity clientPlayer, PlayerModelPart part)
    {
        return SkyBlockAPIViewerScreen.renderSecondLayer || clientPlayer.isWearing(part);
    }
}