package com.stevekung.skyblockcatia.gui.screen;

import com.stevekung.stevekungslib.utils.JsonUtils;

import net.minecraft.client.gui.screen.ErrorScreen;

public class SkyBlockcatiaErrorScreen extends ErrorScreen
{
    private final String text1;
    private final String text2;

    public SkyBlockcatiaErrorScreen(String title, String text1, String text2)
    {
        super(JsonUtils.create(title), null);
        this.text1 = text1;
        this.text2 = text2;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        int offset = 75;
        this.drawCenteredString(this.font, this.text1, this.width / 2, offset, 0xFFFFFF);
        offset += 20;
        this.drawCenteredString(this.font, this.text2, this.width / 2, offset, 0xFFFFFF);
    }
}