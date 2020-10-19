package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen
{
    private MixinChatScreen()
    {
        super(null);
    }

    //    @Override
    //    public void sendMessage(String text)
    //    {
    //        //        if (SkyBlockcatiaMod.isIndicatiaLoaded)TODO
    //        //        {
    //        //            this.sendMessage(IndicatiaIntegration.sendMessage(text), true);
    //        //        }
    //        //        else
    //        //        {
    //        //            super.sendMessage(text);
    //        //        }
    //    }
}