package com.stevekung.skyblockcatia.gui;

import com.stevekung.skyblockcatia.utils.NumberUtils;

import net.minecraft.client.gui.FontRenderer;

public class GuiNumberField extends GuiRightClickTextField
{
    public GuiNumberField(int id, FontRenderer font, int x, int y, int width, int height)
    {
        super(id, font, x, y, width, height);
    }

    @Override
    public void writeText(String textToWrite)
    {
        if (NumberUtils.isNumeric(textToWrite))
        {
            super.writeText(textToWrite);
        }
    }
}