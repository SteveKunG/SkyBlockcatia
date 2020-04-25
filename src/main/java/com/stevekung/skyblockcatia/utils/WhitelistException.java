package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class WhitelistException extends CustomModLoadingErrorDisplayException
{
    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {}

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseX, int mouseY, float partialTicks)
    {
        errorScreen.drawDefaultBackground();
        int offset = 75;

        errorScreen.drawCenteredString(fontRenderer, "SkyBlockcatia couldn't connect to GitHub database", errorScreen.width / 2, offset, 0xFFFFFF);
        offset += 20;
        errorScreen.drawCenteredString(fontRenderer, "Please restart your game or check website status: https://www.githubstatus.com/", errorScreen.width / 2, offset, 0xFFFFFF);
    }
}