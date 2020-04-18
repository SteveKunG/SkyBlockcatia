package stevekung.mods.indicatia.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiRightClickTextField extends GuiTextField
{
    public GuiRightClickTextField(int id, FontRenderer fontRenderer, int x, int y, int width, int height)
    {
        super(id, fontRenderer, x, y, width, height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean flag = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.isFocused() && flag && mouseButton == 1)
        {
            this.setText("");
        }
    }
}