package stevekung.mods.indicatia.gui;

import net.minecraft.client.gui.FontRenderer;
import stevekung.mods.indicatia.utils.NumberUtils;

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