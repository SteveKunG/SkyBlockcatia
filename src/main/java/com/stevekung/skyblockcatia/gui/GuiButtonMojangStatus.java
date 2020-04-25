package com.stevekung.skyblockcatia.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonMojangStatus extends GuiButton
{
    private static final ResourceLocation MOJANG_TEXTURES = new ResourceLocation("skyblockcatia:textures/gui/mojang.png");

    public GuiButtonMojangStatus(int buttonID, int xPos, int yPos)
    {
        super(buttonID, xPos, yPos, 20, 20, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(GuiButtonMojangStatus.MOJANG_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 20 : 0, 0, this.width, this.height, 40, 20);
        }
    }
}