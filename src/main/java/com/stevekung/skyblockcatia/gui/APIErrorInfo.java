package com.stevekung.skyblockcatia.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.IReorderingProcessor;

public class APIErrorInfo extends ScrollingListScreen
{
    private final List<IReorderingProcessor> error;

    public APIErrorInfo(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<String> error)
    {
        super(parent, width, height, top, bottom, left, slotHeight);
        List<IReorderingProcessor> errorList = new ArrayList<>();

        for (String errorLog : error)
        {
            errorList.addAll(this.font.trimStringToWidth(TextComponentUtils.component(errorLog), parent.width - 100));
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
        this.font.func_238407_a_(matrixStack, stat, 40, top, 16777215);
    }
}