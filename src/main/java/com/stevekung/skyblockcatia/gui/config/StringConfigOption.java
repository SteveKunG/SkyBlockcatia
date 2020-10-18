package com.stevekung.skyblockcatia.gui.config;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.gui.widget.button.ExtendedButton;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public class StringConfigOption extends ExtendedConfigOption
{
    private final BiConsumer<SBExtendedConfig, Integer> getter;
    private final BiFunction<SBExtendedConfig, StringConfigOption, String> setter;

    public StringConfigOption(String key, BiConsumer<SBExtendedConfig, Integer> getter, BiFunction<SBExtendedConfig, StringConfigOption, String> setter)
    {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Widget createOptionButton(int x, int y, int width)
    {
        return new ExtendedButton(x, y, width, 20, this.get(), button ->
        {
            this.set(1);
            button.setMessage(this.get());
        });
    }

    public void set(int value)
    {
        this.getter.accept(SBExtendedConfig.INSTANCE, value);
        SBExtendedConfig.INSTANCE.save();
    }

    public ITextComponent get()
    {
        return TextComponentUtils.component(this.setter.apply(SBExtendedConfig.INSTANCE, this));
    }
}