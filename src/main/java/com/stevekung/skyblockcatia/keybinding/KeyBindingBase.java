package com.stevekung.skyblockcatia.keybinding;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingBase extends KeyBinding
{
    public KeyBindingBase(String description, int keyCode)
    {
        super(description, keyCode, "key.skyblockcatia.category");
    }
}