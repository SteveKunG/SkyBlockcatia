package com.stevekung.skyblockcatia.mixin.fabric.gui;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(Gui.class)
public class MixinGui
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At(value = "FIELD", target = "net/minecraft/client/Options.hideGui:Z", shift = At.Shift.BEFORE))
    private void renderHUD(PoseStack poseStack, float partialTicks, CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.onPreInfoRender(poseStack, this.minecraft.getWindow());
    }
}