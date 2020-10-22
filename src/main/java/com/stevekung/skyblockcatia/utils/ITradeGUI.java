package com.stevekung.skyblockcatia.utils;

import com.stevekung.skyblockcatia.gui.GuiNumberField;

import net.minecraft.client.gui.GuiTextField;

public interface ITradeGUI extends ITabComplete
{
    GuiNumberField getNumberField();
    GuiTextField getChatTextField();
}