package com.stevekung.skyblockcatia.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonCustomize extends GuiButton
{
    private static final ResourceLocation main = new ResourceLocation("skyblockcatia:textures/gui/main_lobby.png");
    private static final ResourceLocation play = new ResourceLocation("skyblockcatia:textures/gui/play_icon.png");
    private final boolean isPlay;
    private final String tooltips;
    public String command;
    private static int buttonId = 1000;

    public GuiButtonCustomize(int parentWidth, String tooltips, String command, boolean isPlay)
    {
        super(buttonId++, parentWidth - 130, 20, 20, 20, "");
        this.isPlay = isPlay;
        this.tooltips = tooltips;
        this.command = command.startsWith("/") ? command : isPlay ? "/play " + command : "/lobby " + command;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(this.isPlay ? GuiButtonCustomize.play : GuiButtonCustomize.main);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 20 : 0, 0, this.width, this.height, 40, 20);
        }
    }

    public String getTooltips()
    {
        return this.tooltips;
    }
}