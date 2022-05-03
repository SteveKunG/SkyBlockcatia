package com.stevekung.skyblockcatia.mixin.render_player_api.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;
import com.stevekung.skyblockcatia.renderer.LayerGlowingSteveKunG;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

@Mixin(RenderPlayer.class)
public class RenderPlayerMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/RenderManager;Z)V", at = @At("RETURN"))
    private void init(RenderManager renderManager, boolean useSmallArms, CallbackInfo info) {
        ((RenderPlayer) (Object) this).addLayer(new LayerGlowingSteveKunG((RenderPlayer) (Object) this));
    }

    @Redirect(method = "setModelVisibilities", remap = false, at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isWearing(Lnet/minecraft/entity/player/EnumPlayerModelParts;)Z"))
    private boolean fixSecondLayer(AbstractClientPlayer clientPlayer, EnumPlayerModelParts part) {
        return SkyBlockAPIViewerScreen.renderSecondLayer || clientPlayer.isWearing(part);
    }
}