package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public enum PlayerCountMode
{
    TAB_LIST, HUD;

    private static final PlayerCountMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(PlayerCountMode::ordinal)).toArray(id -> new PlayerCountMode[id]);

    public static PlayerCountMode byId(int id)
    {
        return VALUES[Math.floorMod(id, VALUES.length)];
    }
}