package com.stevekung.skyblockcatia.config;

import java.util.Locale;

public class EnumPotionStatus
{
    public enum Style
    {
        DEFAULT, ICON_AND_TIME;

        private static final Style[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase(Locale.ROOT);
        }
    }

    public enum Position
    {
        LEFT, RIGHT, HOTBAR_LEFT, HOTBAR_RIGHT;

        private static final Position[] values = values();

        public static String getById(int mode)
        {
            return values[mode].toString().toLowerCase(Locale.ROOT);
        }
    }
}