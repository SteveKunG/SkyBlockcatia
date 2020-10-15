package com.stevekung.skyblockcatia.config;

public enum PlayerCountMode
{
    TAB_LIST, HUD;

    private static final PlayerCountMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase();
    }
}