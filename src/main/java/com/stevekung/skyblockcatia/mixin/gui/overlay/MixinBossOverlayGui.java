package com.stevekung.skyblockcatia.mixin.gui.overlay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.gui.overlay.BossOverlayGui;

@Mixin(BossOverlayGui.class)
public class MixinBossOverlayGui
{
    @Inject(method = "shouldDarkenSky()Z", cancellable = true, at = @At("HEAD"))
    private void disableDarkenSky(CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            info.setReturnValue(false);
        }
    }
}