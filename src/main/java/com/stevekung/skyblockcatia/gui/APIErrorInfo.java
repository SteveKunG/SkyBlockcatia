package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

public class APIErrorInfo extends ScrollingListScreen
{
    private final List<IReorderingProcessor> error;

    public APIErrorInfo(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<ITextComponent> error)
    {
        super(parent, width, height, top, bottom, left, slotHeight);
        List<IReorderingProcessor> errorList = Lists.newArrayList();

        for (ITextComponent errorLog : error)
        {
            errorList.addAll(this.font.trimStringToWidth(errorLog, parent.width - 100));
        }
        this.error = errorList;
    }

    @Override
    protected int getSize()
    {
        return this.error.size();
    }

    @Override
    protected void drawPanel(MatrixStack matrixStack, int index, int left, int right, int top)
    {
        IReorderingProcessor stat = this.error.get(index);
        this.font.drawTextWithShadow(matrixStack, stat, 40, top, 16777215);
    }
}