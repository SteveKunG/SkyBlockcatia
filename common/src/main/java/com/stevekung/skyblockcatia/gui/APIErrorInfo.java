package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class APIErrorInfo extends ScrollingListScreen
{
    private final List<FormattedCharSequence> error;

    public APIErrorInfo(Screen parent, int width, int height, int top, int bottom, int left, int slotHeight, List<Component> error)
    {
        super(width, height, top, bottom, left, slotHeight);
        var errorList = Lists.<FormattedCharSequence>newArrayList();

        for (var errorLog : error)
        {
            errorList.addAll(this.font.split(errorLog, parent.width - 100));
        }
        this.error = errorList;
    }

    @Override
    protected int getSize()
    {
        return this.error.size();
    }

    @Override
    protected void drawPanel(PoseStack poseStack, int index, int left, int right, int top)
    {
        var stat = this.error.get(index);
        this.font.drawShadow(poseStack, stat, 40, top, 16777215);
    }
}