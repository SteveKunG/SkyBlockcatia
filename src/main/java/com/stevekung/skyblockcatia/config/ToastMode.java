package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public enum ToastMode
{
    CHAT, TOAST, CHAT_AND_TOAST, DISABLED;

    private static final ToastMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(ToastMode::ordinal)).toArray(id -> new ToastMode[id]);

    public static ToastMode byId(int id)
    {
        return VALUES[Math.floorMod(id, VALUES.length)];
    }
}