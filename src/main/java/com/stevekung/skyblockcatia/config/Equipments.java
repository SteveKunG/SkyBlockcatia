package com.stevekung.skyblockcatia.config;

import java.util.Arrays;
import java.util.Comparator;

public class Equipments
{
    public enum Ordering
    {
        DEFAULT, REVERSE;

        private static final Ordering[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Ordering::ordinal)).toArray(id -> new Ordering[id]);

        public static Ordering byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    public enum Direction
    {
        VERTICAL, HORIZONTAL;

        private static final Direction[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Direction::ordinal)).toArray(id -> new Direction[id]);

        public static Direction byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    public enum Status
    {
        DAMAGE_AND_MAX_DAMAGE, PERCENT, ONLY_DAMAGE, NONE, COUNT, COUNT_AND_STACK;

        private static final Status[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Status::ordinal)).toArray(id -> new Status[id]);

        public static Status byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }

    public enum Position
    {
        LEFT, RIGHT, HOTBAR;

        private static final Position[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Position::ordinal)).toArray(id -> new Position[id]);

        public static Position byId(int id)
        {
            return VALUES[Math.floorMod(id, VALUES.length)];
        }
    }
}