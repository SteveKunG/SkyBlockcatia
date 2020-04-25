package com.stevekung.skyblockcatia.gui.config;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.gui.GuiRightClickTextField;

import net.minecraft.client.Minecraft;

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