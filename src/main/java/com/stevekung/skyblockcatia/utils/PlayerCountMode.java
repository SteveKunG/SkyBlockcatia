package com.stevekung.skyblockcatia.utils;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.util.math.MathHelper;

public enum PlayerCountMode
{
    TAB_LIST(0, "player_count.tab_list"),
    HUD(1, "player_count.hud");

    private static final PlayerCountMode[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(PlayerCountMode::getId)).toArray(id -> new PlayerCountMode[id]);
    private final int id;
    private final String key;

    private PlayerCountMode(int id, String key)
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

    public static PlayerCountMode byId(int id)
    {
        return VALUES[MathHelper.normalizeAngle(id, VALUES.length)];
    }
}