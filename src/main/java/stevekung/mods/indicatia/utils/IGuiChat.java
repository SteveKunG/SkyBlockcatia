package stevekung.mods.indicatia.utils;

import java.util.List;

import net.minecraft.client.gui.GuiButton;

public interface IGuiChat
{
    public void initGui(List<GuiButton> buttonList, int width, int height);
    public void drawScreen(List<GuiButton> buttonList, int mouseX, int mouseY, float partialTicks);
    public void updateScreen(List<GuiButton> buttonList, int width, int height);
    public void actionPerformed(GuiButton button);
    public void onGuiClosed();
    public void handleMouseInput(int width, int height);
    public String sendChatMessage(String original);
}