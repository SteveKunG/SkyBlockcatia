package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TextFormatting;

public class APIErrorInfo extends ScrollingListScreen
{
    private final List<String> error;
    private final Screen parent;

    public APIErrorInfo(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<String> error)
    {
        super(parent, width, height, top, bottom, left, slotHeight);
        this.error = error;
        this.parent = parent;
    }

    @Override
    protected int getSize()
    {
        return this.error.size();
    }

    @Override
    protected void drawPanel(int index, int left, int right, int top)
    {
        String stat = this.error.get(index);
        this.parent.drawString(ClientUtils.unicodeFontRenderer, TextFormatting.RED.toString() + (index == 0 ? TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE : "") + stat, 40, top, 16777215);
    }
}