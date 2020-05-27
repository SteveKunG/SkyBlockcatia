package com.stevekung.skyblockcatia.mixin;

import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.GuiChatRegistry;
import com.stevekung.skyblockcatia.utils.IGuiChat;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen
{
    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void initGui(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.initGui(this.buttonList, this.width, this.height));
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("RETURN"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.drawScreen(this.buttonList, mouseX, mouseY, partialTicks));
    }

    @Inject(method = "updateScreen()V", at = @At("RETURN"))
    private void updateScreen(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.updateScreen(this.buttonList, this.width, this.height));
    }

    @Inject(method = "onGuiClosed()V", at = @At("RETURN"))
    private void onGuiClosed(CallbackInfo info)
    {
        GuiChatRegistry.getGuiChatList().forEach(IGuiChat::onGuiClosed);
    }

    @Inject(method = "handleMouseInput()V", at = @At("RETURN"))
    private void handleMouseInput(CallbackInfo info) throws IOException
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.handleMouseInput(this.width, this.height));
    }

    @Inject(method = "mouseClicked(III)V", cancellable = true, at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) throws IOException
    {
        if (mouseButton == 1)
        {
            IChatComponent component = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentRightClick(component))
            {
                info.cancel();
            }
        }
        if (mouseButton == 2)
        {
            IChatComponent component = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentMiddleClick(component))
            {
                info.cancel();
            }
        }
    }

    @Override
    public void sendChatMessage(String msg)
    {
        if (!msg.startsWith("/"))
        {
            for (IGuiChat chat : GuiChatRegistry.getGuiChatList())
            {
                this.sendChatMessage(chat.sendChatMessage(msg), true);
            }
        }
        else
        {
            super.sendChatMessage(msg);
        }
    }

    private boolean handleComponentRightClick(IChatComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();

            if (clickEvent != null)
            {
                if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.setText(clickEvent.getValue().replace("/p", "/visit"), true);
                }
                return true;
            }
            return false;
        }
    }
    
    private boolean handleComponentMiddleClick(IChatComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();

            if (clickEvent != null)
            {
                if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.sendChatMessage(clickEvent.getValue().replace("/p", "/sbapi"), false);
                }
                return true;
            }
            return false;
        }
    }
}