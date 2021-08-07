package com.stevekung.skyblockcatia.mixin.gui;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.GuiChatRegistry;
import com.stevekung.skyblockcatia.utils.IGuiChat;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiChat.class)
public class GuiChatMixin extends GuiScreen
{
    @Inject(method = "initGui()V", at = @At("TAIL"))
    private void initGui(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.initGui(this.buttonList, this.width, this.height));
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("TAIL"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.drawScreen(this.buttonList, mouseX, mouseY, partialTicks));
    }

    @Inject(method = "updateScreen()V", at = @At("TAIL"))
    private void updateScreen(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.updateScreen(this.buttonList, this.width, this.height));
    }

    @Inject(method = "onGuiClosed()V", at = @At("TAIL"))
    private void onGuiClosed(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(IGuiChat::onGuiClosed);
    }

    @Inject(method = "handleMouseInput()V", at = @At("TAIL"))
    private void handleMouseInput(CallbackInfo info) throws IOException
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.handleMouseInput(this.width, this.height));
    }
}