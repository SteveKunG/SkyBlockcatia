package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class NoCurlException extends CustomModLoadingErrorDisplayException
{
    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {}

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks)
    {
        errorScreen.drawDefaultBackground();
        int offset = 75;

        errorScreen.drawCenteredString(fontRenderer, "SkyBlockcatia detected no cURL module installed on your windows", errorScreen.width / 2, offset, 0xFFFFFF);
        offset += 20;
        errorScreen.drawCenteredString(fontRenderer, "Please install cURL before using this mod!", errorScreen.width / 2, offset, 0xFFFFFF);
    }
}