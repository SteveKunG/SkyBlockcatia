package com.stevekung.skyblockcatia.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class RightClickTextFieldWidget extends TextFieldWidget
{
    public RightClickTextFieldWidget(int x, int y, int width, int height)
    {
        super(Minecraft.getInstance().fontRenderer, x, y, width, height, StringTextComponent.EMPTY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (this.isFocused() && flag && mouseButton == 1)
        {
            this.setText("");
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}