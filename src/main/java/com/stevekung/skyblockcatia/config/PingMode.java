package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public enum PingMode
{
    ONLY_PING, PING_AND_DELAY;

    private static final PingMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(PingMode::ordinal)).toArray(id -> new PingMode[id]);

    public static PingMode byId(int id)
    {
        return VALUES[Math.floorMod(id, VALUES.length)];
    }
}