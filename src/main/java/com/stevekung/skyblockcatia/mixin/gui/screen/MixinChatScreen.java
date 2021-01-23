package com.stevekung.skyblockcatia.mixin.gui.screen;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.integration.IndicatiaIntegration;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

@Mixin(ChatScreen.class)
public class MixinChatScreen extends Screen
{
    private MixinChatScreen()
    {
        super(null);
    }

    @Override
    public void sendMessage(String text)
    {
        if (CompatibilityUtils.isIndicatiaLoaded)
        {
            this.sendMessage(IndicatiaIntegration.sendMessage(text), true);
        }
        else
        {
            super.sendMessage(text);
        }
    }
}