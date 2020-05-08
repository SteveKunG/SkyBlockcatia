package com.stevekung.skyblockcatia.gui.config;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.gui.widget.ExtendedTextFieldWidget;

import net.minecraft.client.gui.widget.Widget;

public class TextFieldConfigOption extends ExtendedConfigOption
{
    private final Function<SBExtendedConfig, String> getter;
    private final BiConsumer<SBExtendedConfig, String> setter;

    public TextFieldConfigOption(String key, Function<SBExtendedConfig, String> getter, BiConsumer<SBExtendedConfig, String> setter)
    {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Widget createOptionButton(int x, int y, int width)
    {
        ExtendedTextFieldWidget textField = new ExtendedTextFieldWidget(x, y, width, this);
        this.set(textField.getText());
        textField.setText(this.get());
        textField.setDisplayName(this.getDisplayName());
        textField.setDisplayPrefix(this.getDisplayPrefix());
        return textField;
    }

    public void set(String value)
    {
        this.setter.accept(SBExtendedConfig.INSTANCE, value);
    }

    public String get()
    {
        return this.getter.apply(SBExtendedConfig.INSTANCE);
    }
}