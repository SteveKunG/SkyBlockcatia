package com.stevekung.skyblockcatia.utils;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.Mth;

public enum HitboxRenderMode
{
    DEFAULT(0, "hitbox_render_mode.default"),
    DRAGON(1, "hitbox_render_mode.dragon"),
    CRYSTAL(2, "hitbox_render_mode.crystal"),
    DRAGON_AND_CRYSTAL(3, "hitbox_render_mode.dragon_and_crystal");

    private static final HitboxRenderMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(HitboxRenderMode::getId)).toArray(HitboxRenderMode[]::new);
    private final int id;
    private final String key;

    HitboxRenderMode(int id, String key)
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

    public static HitboxRenderMode byId(int id)
    {
        return VALUES[Mth.positiveModulo(id, VALUES.length)];
    }
}