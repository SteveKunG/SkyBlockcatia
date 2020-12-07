package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;

import net.minecraft.client.gui.GuiIngame;

@Mixin(GuiIngame.class)
public class GuiIngameMixin
{
    @Inject(method = "func_181029_i()V", at = @At("RETURN"))
    private void resetToast(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().clear();
    }
}