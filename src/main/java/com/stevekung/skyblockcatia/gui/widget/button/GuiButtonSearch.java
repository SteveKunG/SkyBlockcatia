package com.stevekung.skyblockcatia.gui.widget.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSearch extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/search.png");

    public GuiButtonSearch(int buttonID, int xPos, int yPos)
    {
        super(buttonID, xPos, yPos, 18, 18, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, this.enabled && flag ? 18 : !this.enabled ? 36 : 0, 0, this.width, this.height, 54, 18);
        }
    }
}