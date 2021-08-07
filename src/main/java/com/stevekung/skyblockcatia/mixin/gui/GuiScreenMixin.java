package com.stevekung.skyblockcatia.mixin.gui;

import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.gui.SignSelectionList;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiEditSign;

@Mixin(GuiScreen.class)
public class GuiScreenMixin
{
    @Inject(method = "confirmClicked(ZI)V", at = @At("HEAD"))
    private void confirmClicked(boolean result, int id, CallbackInfo info)
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)screen;

            if (result)
            {
                String text = sign.tileSign.signText[0].getUnformattedText();
                sign.tileSign.markDirty();
                SignSelectionList.processSignData(sign.tileSign);
                ((IEditSign)sign).getSignSelectionList().add(text);
                screen.mc.displayGuiScreen(null);
            }
            else
            {
                screen.mc.displayGuiScreen(screen);
            }
        }
    }

    @Inject(method = "mouseClicked(III)V", at = @At("HEAD"))
    private void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) throws IOException
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)screen;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList && ((IEditSign)sign).getSignSelectionList() != null)
            {
                ((IEditSign)sign).getSignSelectionList().mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Inject(method = "mouseReleased(III)V", at = @At("HEAD"))
    private void mouseReleased(int mouseX, int mouseY, int state, CallbackInfo info) throws IOException
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)screen;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList && ((IEditSign)sign).getSignSelectionList() != null)
            {
                ((IEditSign)sign).getSignSelectionList().mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Inject(method = "handleMouseInput()V", at = @At("HEAD"))
    private void handleMouseInput(CallbackInfo info) throws IOException
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiEditSign)
        {
            GuiEditSign sign = (GuiEditSign)screen;

            if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaConfig.enableSignSelectionList && ((IEditSign)sign).getSignSelectionList() != null)
            {
                ((IEditSign)sign).getSignSelectionList().handleMouseInput();
            }
        }
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo info) throws IOException
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)screen;

            if (button.id == 155)
            {
                String text = chest.lowerChestInventory.getDisplayName().getUnformattedText();
                ClientUtils.printClientMessage(JsonUtils.create("Copied seller auction command!").setChatStyle(JsonUtils.green()));
                GuiScreen.setClipboardString("/ah " + text.replace(text.substring(text.indexOf('\'')), ""));
            }
            else if (button.id == 156)
            {
                String text = chest.lowerChestInventory.getDisplayName().getUnformattedText();
                screen.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.GuiState.PLAYER, text.replace(text.substring(text.indexOf('\'')), ""), "", ""));
            }
        }
    }

    @Inject(method = "setText(Ljava/lang/String;Z)V", at = @At("HEAD"))
    private void setText(String newChatText, boolean shouldOverwrite, CallbackInfo info)
    {
        GuiScreen screen = (GuiScreen) (Object) this;

        if (screen instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)screen;

            if (GuiScreenUtils.isChatable(chest.lowerChestInventory))
            {
                if (shouldOverwrite)
                {
                    ((IExtendedChatGui)chest).getChatTextField().setText(newChatText);
                }
                else
                {
                    ((IExtendedChatGui)chest).getChatTextField().writeText(newChatText);
                }
            }
        }
    }
}