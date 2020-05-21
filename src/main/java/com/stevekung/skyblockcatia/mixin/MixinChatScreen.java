package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen
{
    public MixinChatScreen(String defaultText)
    {
        super(NarratorChatListener.EMPTY);
    }

    @Inject(method = "mouseClicked(DDI)Z", cancellable = true, at = @At("HEAD"))
    private void mouseClicked(double mouseX, double mouseY, int mouseButton, CallbackInfoReturnable<Boolean> info)
    {
        if (mouseButton == 1)
        {
            ITextComponent component = this.minecraft.ingameGUI.getChatGUI().getTextComponent(mouseX, mouseY);

            if (component != null && this.handleComponentRightClicked(component))
            {
                info.setReturnValue(true);
            }
        }
    }

    private boolean handleComponentRightClicked(ITextComponent component)
    {
        ClickEvent clickEvent = component.getStyle().getClickEvent();

        if (clickEvent != null)
        {
            if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
            {
                this.insertText(clickEvent.getValue().replace("/p", "/visit"), true);
            }
            return true;
        }
        return false;
    }
}