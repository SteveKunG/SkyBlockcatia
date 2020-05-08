package com.stevekung.skyblockcatia.utils.skyblock.api;

import net.minecraft.util.text.TextFormatting;

public class HypixelRank
{
    public enum Base
    {
        NONE("", TextFormatting.GRAY),
        REGULAR("", TextFormatting.GRAY),
        VIP("VIP", TextFormatting.GREEN),
        VIP_PLUS("VIP", TextFormatting.GREEN),
        MVP("MVP", TextFormatting.AQUA),
        MVP_PLUS("MVP", TextFormatting.AQUA);

        private final String name;
        private final TextFormatting color;

        private Base(String name, TextFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public TextFormatting getColor()
        {
            return this.color;
        }
    }

    public enum Type
    {
        NORMAL("", TextFormatting.GRAY),
        ADMIN("ADMIN", TextFormatting.RED),
        YOUTUBER("YOUTUBE", TextFormatting.RED),
        HELPER("HELPER", TextFormatting.BLUE),
        MODERATOR("MOD", TextFormatting.DARK_GREEN);

        private final String name;
        private final TextFormatting color;

        private Type(String name, TextFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public TextFormatting getColor()
        {
            return this.color;
        }
    }
}