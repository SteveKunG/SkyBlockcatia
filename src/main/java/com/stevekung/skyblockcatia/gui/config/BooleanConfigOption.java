package com.stevekung.skyblockcatia.gui.config;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.gui.widget.button.ExtendedButton;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class BooleanConfigOption extends ExtendedConfigOption
{
    private final Predicate<SBExtendedConfig> getter;
    private final BiConsumer<SBExtendedConfig, Boolean> setter;
    private boolean yesNo;

    public BooleanConfigOption(String key, Predicate<SBExtendedConfig> getter, BiConsumer<SBExtendedConfig, Boolean> setter)
    {
        super(key);
        this.getter = getter;
        this.setter = setter;
    }

    public BooleanConfigOption(String key, Predicate<SBExtendedConfig> getter, BiConsumer<SBExtendedConfig, Boolean> setter, boolean yesNo)
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
        SBExtendedConfig.INSTANCE.save();
    }

    private void set(boolean value)
    {
        this.setter.accept(SBExtendedConfig.INSTANCE, value);
    }

    public boolean get()
    {
        return this.getter.test(SBExtendedConfig.INSTANCE);
    }

    public ITextComponent getDisplayString()
    {
        return TextComponentUtils.component(this.getDisplayPrefix() + this.optionsEnabled(this.get()));
    }

    private ITextComponent optionsEnabled(boolean isEnabled)
    {
        return isEnabled ? this.yesNo ? DialogTexts.GUI_YES.deepCopy().mergeStyle(TextFormatting.GREEN) : DialogTexts.OPTIONS_ON.deepCopy().mergeStyle(TextFormatting.GREEN) : this.yesNo ? DialogTexts.GUI_NO.deepCopy().mergeStyle(TextFormatting.RED) : DialogTexts.OPTIONS_OFF.deepCopy().mergeStyle(TextFormatting.RED);
    }
}