package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin extends Gui
{
    @Inject(method = "drawChat(I)V", at = @At("HEAD"))
    private void renderDepthPre(int updateCounter, CallbackInfo info)
    {
        GlStateManager.disableDepth();
    }

    @Inject(method = "drawChat(I)V", at = @At("RETURN"))
    private void renderDepthPost(int updateCounter, CallbackInfo info)
    {
        GlStateManager.enableDepth();
    }

    @ModifyConstant(method = "drawChat(I)V", constant = @Constant(floatValue = 20.0F))
    private float modifyChatHeight(float defaultValue)
    {
        return SkyBlockcatiaMod.isPatcherLoaded ? defaultValue : 8.0F;
    }

    @ModifyConstant(method = "getChatComponent(II)Lnet/minecraft/util/IChatComponent;", constant = @Constant(intValue = 3))
    private int modifyChatMouseX(int defaultValue)
    {
        return SkyBlockcatiaMod.isPatcherLoaded ? defaultValue : 2;
    }

    @ModifyConstant(method = "getChatComponent(II)Lnet/minecraft/util/IChatComponent;", constant = @Constant(intValue = 27))
    private int modifyChatMouseY(int defaultValue)
    {
        return SkyBlockcatiaMod.isPatcherLoaded ? defaultValue : 40;
    }
}