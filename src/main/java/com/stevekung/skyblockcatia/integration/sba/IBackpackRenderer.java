package com.stevekung.skyblockcatia.integration.sba;

import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockAPIViewer;

public interface IBackpackRenderer
{
    boolean isFreezeBackpack();
    void keyTyped(int keyCode);
    void drawBackpacks(GuiSkyBlockAPIViewer gui, int mouseX, int mouseY, float partialTicks);
    void clearRenderBackpack();
}