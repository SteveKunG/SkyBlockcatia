package com.stevekung.skyblockcatia.mixin.gui.components;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.gui.components.BossHealthOverlay;

@Mixin(BossHealthOverlay.class)
public class MixinBossHealthOverlay
{
    @Inject(method = "shouldDarkenScreen()Z", cancellable = true, at = @At("HEAD"))
    private void shouldDarkenScreen(CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            info.setReturnValue(false);
        }
    }
}