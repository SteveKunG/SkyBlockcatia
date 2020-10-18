package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;

public class APIErrorInfo extends ScrollingListScreen
{
    private final List<String> error;

    public APIErrorInfo(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<String> error)
    {
        super(parent, width, height, top, bottom, left, slotHeight);
        this.error = error;
    }

    @Override
    protected int getSize()
    {
        return this.error.size();
    }

    @Override
    protected void drawPanel(MatrixStack matrixStack, int index, int left, int right, int top)
    {
        String stat = this.error.get(index);
        AbstractGui.drawString(matrixStack, this.mc.fontRenderer, TextFormatting.RED.toString() + (index == 0 ? TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE : "") + stat, 40, top, 16777215);
    }
}