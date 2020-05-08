package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.math.MathHelper;

public enum ToastMode
{
    CHAT(0, "skyblockcatia.chat"),
    TOAST(1, "skyblockcatia.toast"),
    CHAT_AND_TOAST(2, "skyblockcatia.chat_and_toast"),
    DISABLED(3, "skyblockcatia.disabled");

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