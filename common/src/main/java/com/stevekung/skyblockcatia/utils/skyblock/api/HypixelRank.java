package com.stevekung.skyblockcatia.utils.skyblock.api;

import net.minecraft.ChatFormatting;

public class HypixelRank
{
    public enum Base
    {
        NONE("", ChatFormatting.GRAY),
        REGULAR("", ChatFormatting.GRAY),
        VIP("VIP", ChatFormatting.GREEN),
        VIP_PLUS("VIP", ChatFormatting.GREEN),
        MVP("MVP", ChatFormatting.AQUA),
        MVP_PLUS("MVP", ChatFormatting.AQUA);

        private final String name;
        private final ChatFormatting color;

        Base(String name, ChatFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public ChatFormatting getColor()
        {
            return this.color;
        }
    }

    public enum Type
    {
        NORMAL("", ChatFormatting.GRAY),
        ADMIN("ADMIN", ChatFormatting.RED),
        YOUTUBER("YOUTUBE", ChatFormatting.RED),
        HELPER("HELPER", ChatFormatting.BLUE),
        MODERATOR("MOD", ChatFormatting.DARK_GREEN);

        private final String name;
        private final ChatFormatting color;

        Type(String name, ChatFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public ChatFormatting getColor()
        {
            return this.color;
        }
    }
}