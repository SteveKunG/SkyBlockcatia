package com.stevekung.skyblockcatia.gui.config.widget;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiConfigButton extends GuiButton
{
    private final SkyBlockcatiaSettings.Options options;

    public GuiConfigButton(int id, int x, int y, String text)
    {
        this(id, x, y, 150, null, text);
    }

    public GuiConfigButton(int id, int x, int y, int width, SkyBlockcatiaSettings.Options options, String text)
    {
        super(id, x, y, width, 20, text);
        this.options = options;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int state = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + state * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + state * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (this.packedFGColour != 0)
            {
                color = this.packedFGColour;
            }
            else
            {
                if (!this.enabled)
                {
                    color = 10526880;
                }
                else if (this.hovered)
                {
                    color = 16777120;
                }
            }
            boolean smallText = this.width <= 200 && this.displayString.length() > 32;
            this.drawCenteredString(smallText ? ColorUtils.unicodeFontRenderer : mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, color);
        }
    }

    public SkyBlockcatiaSettings.Options getOption()
    {
        return this.options;
    }
}