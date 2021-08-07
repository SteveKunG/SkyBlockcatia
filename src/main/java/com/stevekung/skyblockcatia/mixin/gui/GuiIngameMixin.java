package com.stevekung.skyblockcatia.mixin.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;

import net.minecraft.client.gui.GuiIngame;

@Mixin(GuiIngame.class)
public class GuiIngameMixin
{
    @Inject(method = "resetPlayersOverlayFooterHeader()V", at = @At("TAIL"))
    private void resetToast(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().clear();
    }
}