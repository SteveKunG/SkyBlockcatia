package com.stevekung.skyblockcatia.gui.widget;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.gui.config.TextFieldConfigOption;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

public class ExtendedTextFieldWidget extends TextFieldWidget
{
    private final TextFieldConfigOption textFieldOption;
    private String displayName;
    private String displayPrefix;

    public ExtendedTextFieldWidget(int x, int y, int width, TextFieldConfigOption textFieldOption)
    {
        super(Minecraft.getInstance().fontRenderer, x, y, width, 20, StringTextComponent.EMPTY);
        this.textFieldOption = textFieldOption;
        this.setText(textFieldOption.get());
        this.setVisible(true);
        this.setMaxStringLength(13);
    }

    public void setValue(String value)
    {
        this.textFieldOption.set(value);
        SBExtendedConfig.INSTANCE.save();
    }

    public void setDisplayName(String name)
    {
        this.displayName = name;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayPrefix(String name)
    {
        this.displayPrefix = name;
    }

    public String getDisplayPrefix()
    {
        return this.displayPrefix;
    }
}