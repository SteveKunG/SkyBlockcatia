package com.stevekung.skyblockcatia.gui.widget.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiButtonCustomize extends GuiButton
{
    private static final ResourceLocation MAIN = new ResourceLocation("skyblockcatia:textures/gui/main_lobby.png");
    private static final ResourceLocation PLAY = new ResourceLocation("skyblockcatia:textures/gui/play_icon.png");
    private static final ResourceLocation BLANK = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private final boolean isPlay;
    private final String tooltips;
    private final ItemStack skull;
    public String command;
    private static int buttonId = 1000;

    public GuiButtonCustomize(int parentWidth, String tooltips, String command, boolean isPlay, ItemStack skull)
    {
        super(buttonId++, parentWidth - 130, 20, 20, 20, "");
        this.isPlay = isPlay;
        this.tooltips = tooltips;
        this.command = command.startsWith("/") ? command : isPlay ? "/play " + command : "/lobby " + command;
        this.skull = skull;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(this.skull == null ? this.isPlay ? PLAY : MAIN : BLANK);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 20 : 0, 0, this.width, this.height, 40, 20);

            if (this.skull != null)
            {
                GlStateManager.enableDepth();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(this.skull, this.xPosition + 2, this.yPosition + 2);
            }
        }
    }

    public String getTooltips()
    {
        return this.tooltips;
    }
}