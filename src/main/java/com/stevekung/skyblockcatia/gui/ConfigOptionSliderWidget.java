package com.stevekung.skyblockcatia.gui;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.gui.config.DoubleConfigOption;

public class ConfigOptionSliderWidget extends ConfigSliderWidget
{
    private final DoubleConfigOption option;

    public ConfigOptionSliderWidget(int x, int y, int width, int height, DoubleConfigOption doubleOpt)
    {
        super(x, y, width, height, (float)doubleOpt.normalizeValue(doubleOpt.get()));
        this.option = doubleOpt;
        this.updateMessage();
    }

    @Override
    protected void applyValue()
    {
        this.option.set(this.option.denormalizeValue(this.value));
        ExtendedConfig.INSTANCE.save();
    }

    @Override
    protected void updateMessage()
    {
        this.setMessage(this.option.getDisplayString());
    }
}