package com.stevekung.skyblockcatia.config;

import java.util.Locale;

public enum ToastMode
{
    CHAT, TOAST, CHAT_AND_TOAST, DISABLED;

    private static final ToastMode[] values = values();

    public static String getById(int mode)
    {
        return values[mode].toString().toLowerCase(Locale.ROOT);
    }
}