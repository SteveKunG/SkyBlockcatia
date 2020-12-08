package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public enum HitboxRenderMode
{
    DEFAULT, DRAGON, CRYSTAL, DRAGON_AND_CRYSTAL;

    private static final HitboxRenderMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(HitboxRenderMode::ordinal)).toArray(id -> new HitboxRenderMode[id]);

    public static HitboxRenderMode byId(int id)
    {
        return VALUES[Math.floorMod(id, VALUES.length)];
    }
}