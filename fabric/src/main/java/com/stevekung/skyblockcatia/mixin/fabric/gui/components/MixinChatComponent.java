package com.stevekung.skyblockcatia.mixin.fabric.gui.components;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;

@Mixin(ChatComponent.class)
public class MixinChatComponent
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "render", cancellable = true, at = @At("HEAD"))
    private void render(PoseStack poseStack, int ticks, CallbackInfo info)
    {
        if (this.minecraft.screen != null && this.minecraft.screen instanceof ContainerScreen)
        {
            ContainerScreen chest = (ContainerScreen)this.minecraft.screen;

            if (MainEventHandler.showChat && GuiScreenUtils.isChatable(chest.getTitle()))
            {
                info.cancel();
            }
        }
    }
}