package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen
{
    protected MixinChatScreen(ITextComponent title)
    {
        super(title);
    }

    @Override
    public void sendMessage(String msg)
    {
        this.sendMessage(msg, true);

        //        if (!msg.startsWith("/"))TODO
        //        {
        //            for (IGuiChat chat : GuiChatRegistry.getGuiChatList())
        //            {
        //                this.sendChatMessage(chat.sendChatMessage(msg), true);
        //            }
        //        }
        //        else
        //        {
        //            super.sendChatMessage(msg);
        //        }
    }
}