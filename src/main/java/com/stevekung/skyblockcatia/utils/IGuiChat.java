package com.stevekung.skyblockcatia.utils;

import java.util.List;

import net.minecraft.client.gui.GuiButton;

public interface IGuiChat
{
    void initGui(List<GuiButton> buttonList, int width, int height);
    void drawScreen(List<GuiButton> buttonList, int mouseX, int mouseY, float partialTicks);
    void updateScreen(List<GuiButton> buttonList, int width, int height);
    void actionPerformed(GuiButton button);
    void onGuiClosed();
    void handleMouseInput(int width, int height);
    String sendChatMessage(String original);
}