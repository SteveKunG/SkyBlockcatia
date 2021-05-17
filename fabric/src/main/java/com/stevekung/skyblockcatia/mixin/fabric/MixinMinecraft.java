package com.stevekung.skyblockcatia.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    @Inject(method = "runTick", at = @At(value = "INVOKE_STRING", target = "net/minecraft/util/profiling/ProfilerFiller.popPush(Ljava/lang/String;)V", args = {"ldc=gameRenderer"}))
    private void renderTicks(boolean bl, CallbackInfo info)
    {
        if (MainEventHandler.playerToView != null)
        {
            ((Minecraft) (Object) this).setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, MainEventHandler.playerToView, "", ""));
            MainEventHandler.playerToView = null;
        }
    }
}