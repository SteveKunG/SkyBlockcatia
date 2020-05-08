package com.stevekung.skyblockcatia.gui.config;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.gui.ExtendedButton;
import com.stevekung.stevekungslib.utils.LangUtils;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.TextFormatting;

public class BooleanConfigOption extends ExtendedConfigOption
{
    private final Predicate<ExtendedConfig> getter;
    private final BiConsumer<ExtendedConfig, Boolean> setter;
    private boolean yesNo;

    public BooleanConfigOption(String key, Predicate<ExtendedConfig> getter, BiConsumer<ExtendedConfig, Boolean> setter)
    {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }

    public BooleanConfigOption(String key, Predicate<ExtendedConfig> getter, BiConsumer<ExtendedConfig, Boolean> setter, boolean yesNo)
    {
        super(key);
        this.getter = getter;
        this.setter = setter;
        this.yesNo = yesNo;
    }

    @Override
    public Widget createOptionButton(int x, int y, int width)
    {
        return new ExtendedButton(x, y, width, 20, this.getDisplayString(), button ->
        {
            this.set();
            button.setMessage(this.getDisplayString());
        });
    }

    public void set(String value)
    {
        this.set("true".equals(value));
    }

    public void set()
    {
        this.set(!this.get());
        ExtendedConfig.INSTANCE.save();
    }

    private void set(boolean value)
    {
        this.setter.accept(ExtendedConfig.INSTANCE, value);
    }

    public boolean get()
    {
        return this.getter.test(ExtendedConfig.INSTANCE);
    }

    public String getDisplayString()
    {
        String on = this.yesNo ? LangUtils.translate("gui.yes") : "ON";
        String off = this.yesNo ? LangUtils.translate("gui.no") : "OFF";
        return this.getDisplayPrefix() + (this.get() ? TextFormatting.GREEN + on : TextFormatting.RED + off);
    }
}