package com.stevekung.skyblockcatia.handler;

import net.minecraft.client.settings.KeyBinding;

public class KeyBindingIU extends KeyBinding
{
    public KeyBindingIU(String description, int keyCode)
    {
        super(description, keyCode, "key.skyblockcatia.category");
    }
}