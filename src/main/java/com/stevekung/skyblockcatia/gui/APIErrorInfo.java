package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.GuiScrollingList;

public class APIErrorInfo extends GuiScrollingList
{
    private final List<String> error;
    private final GuiScreen parent;

    public APIErrorInfo(GuiScreen parent, int width, int height, int top, int bottom, int left, int entryHeight, int parentWidth, int parentHeight, List<String> error)
    {
        super(parent.mc, width, height, top, bottom, left, entryHeight, parentWidth, parentHeight);
        this.error = error;
        this.parent = parent;
    }

    @Override
    protected int getSize()
    {
        return this.error.size();
    }

    @Override
    protected void drawSlot(int index, int right, int top, int height, Tessellator tess)
    {
        String stat = this.error.get(index);
        this.parent.drawString(ColorUtils.unicodeFontRenderer, EnumChatFormatting.RED + "" + (index == 0 ? EnumChatFormatting.BOLD + "" + EnumChatFormatting.UNDERLINE: "") + stat, 40, top, 16777215);
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {}

    @Override
    protected void drawBackground() {}

    @Override
    protected boolean isSelected(int index)
    {
        return false;
    }
}