package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public class StatusEffects
{
    public enum Style
    {
        DEFAULT, ICON_AND_TIME;

        private static final Style[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Style::ordinal)).toArray(id -> new Style[id]);

        public static Style byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    public enum Position
    {
        LEFT, RIGHT, HOTBAR_LEFT, HOTBAR_RIGHT;

        private static final Position[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Position::ordinal)).toArray(id -> new Position[id]);

        public static Position byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }
}