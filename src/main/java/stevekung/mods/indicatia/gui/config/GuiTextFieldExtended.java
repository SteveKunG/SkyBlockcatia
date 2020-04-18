package stevekung.mods.indicatia.gui.config;

import net.minecraft.client.Minecraft;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.gui.GuiRightClickTextField;

public class GuiTextFieldExtended extends GuiRightClickTextField
{
    private final ExtendedConfig.Options options;

    public GuiTextFieldExtended(int id, int x, int y, int width, ExtendedConfig.Options options)
    {
        super(id, Minecraft.getMinecraft().fontRendererObj, x, y, width, 20);
        this.options = options;
        this.setEnabled(true);
        this.setMaxStringLength(13);
    }

    public ExtendedConfig.Options getOption()
    {
        return this.options;
    }
}