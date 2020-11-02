package com.stevekung.skyblockcatia.config;

import java.util.Locale;

public enum PingMode
{
    ONLY_PING, PING_AND_DELAY;

    private static final PingMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase(Locale.ROOT);
    }
}