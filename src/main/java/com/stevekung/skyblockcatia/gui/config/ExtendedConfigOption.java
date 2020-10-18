package com.stevekung.skyblockcatia.gui.config;

import com.stevekung.stevekungslib.utils.LangUtils;

import net.minecraft.client.gui.widget.Widget;

public abstract class ExtendedConfigOption
{
    private final String key;

    public ExtendedConfigOption(String key)
    {
        this.key = "skyblockcatia_config." + key;
    }

    public String getDisplayPrefix()
    {
        return LangUtils.translateString(this.key) + ": ";
    }

    public String getDisplayName()
    {
        return LangUtils.translateString(this.key);
    }

    public abstract Widget createOptionButton(int x, int y, int width);
}