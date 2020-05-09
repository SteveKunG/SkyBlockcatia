package com.stevekung.skyblockcatia.utils;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.math.MathHelper;

public enum ToastMode
{
    CHAT(0, "toast_mode.chat"),
    TOAST(1, "toast_mode.toast"),
    CHAT_AND_TOAST(2, "toast_mode.chat_and_toast"),
    DISABLED(3, "toast_mode.disabled");

    private static final ToastMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(ToastMode::getId)).toArray(id -> new ToastMode[id]);
    private final int id;
    private final String key;

    private ToastMode(int id, String key)
    {
        this.id = id;
        this.key = key;
    }

    public String getTranslationKey()
    {
        return this.key;
    }

    public int getId()
    {
        return this.id;
    }

    public static ToastMode byId(int id)
    {
        return VALUES[MathHelper.normalizeAngle(id, VALUES.length)];
    }
}