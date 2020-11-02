package com.stevekung.skyblockcatia.config;

import java.util.Locale;

public enum PlayerCountMode
{
    TAB_LIST, HUD;

    private static final PlayerCountMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase(Locale.ROOT);
    }
}