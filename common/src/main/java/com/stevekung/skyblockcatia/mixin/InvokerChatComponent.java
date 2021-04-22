package com.stevekung.skyblockcatia.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;

@Mixin(ChatComponent.class)
public interface InvokerChatComponent
{
    @Accessor("trimmedMessages")
    List<GuiMessage<FormattedCharSequence>> getTrimmedMessages();

    @Accessor("chatScrollbarPos")
    int getChatScrollbarPos();

    @Invoker
    boolean invokeIsChatHidden();

    @Invoker
    static double invokeGetTimeFactor(int i)
    {
        throw new Error();
    }
}