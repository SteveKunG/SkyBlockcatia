package com.stevekung.skyblockcatia.utils;

import com.stevekung.skyblockcatia.gui.widget.GuiNumberField;

import net.minecraft.client.gui.GuiTextField;

public interface IExtendedChatGui extends ITabComplete
{
    GuiNumberField getNumberField();
    GuiTextField getChatTextField();
}