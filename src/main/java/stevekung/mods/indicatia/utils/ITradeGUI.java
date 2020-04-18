package stevekung.mods.indicatia.utils;

import stevekung.mods.indicatia.gui.GuiNumberField;

public interface ITradeGUI extends ITabComplete
{
    GuiNumberField getNumberField();
}