package com.stevekung.skyblockcatia.integration.sba;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;

public interface IBackpackRenderer
{
    boolean isFreezeBackpack();
    void keyTyped(int keyCode);
    void drawBackpacks(SkyBlockAPIViewerScreen gui, int mouseX, int mouseY, float partialTicks);
    void clearRenderBackpack();
}