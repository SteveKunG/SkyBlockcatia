package com.stevekung.skyblockcatia.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;

public class RightClickTextFieldWidget extends EditBox
{
    public RightClickTextFieldWidget(int x, int y, int width, int height)
    {
        super(Minecraft.getInstance().font, x, y, width, height, TextComponent.EMPTY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        var flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

        if (this.isFocused() && flag && mouseButton == 1)
        {
            this.setValue("");
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}