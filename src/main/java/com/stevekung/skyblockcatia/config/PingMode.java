package com.stevekung.skyblockcatia.config;

public enum PingMode
{
    ONLY_PING, PING_AND_DELAY;

    private static final PingMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase();
    }
}