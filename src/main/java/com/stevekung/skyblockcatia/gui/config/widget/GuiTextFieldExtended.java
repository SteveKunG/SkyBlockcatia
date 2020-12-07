package com.stevekung.skyblockcatia.gui.config.widget;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.widget.GuiRightClickTextField;

import net.minecraft.client.Minecraft;

public class GuiTextFieldExtended extends GuiRightClickTextField
{
    private final SkyBlockcatiaSettings.Options options;

    public GuiTextFieldExtended(int id, int x, int y, int width, SkyBlockcatiaSettings.Options options)
    {
        super(id, Minecraft.getMinecraft().fontRendererObj, x, y, width, 20);
        this.options = options;
        this.setEnabled(true);
        this.setMaxStringLength(13);
    }

    public SkyBlockcatiaSettings.Options getOption()
    {
        return this.options;
    }
}