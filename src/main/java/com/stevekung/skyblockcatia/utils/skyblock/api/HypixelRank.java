package com.stevekung.skyblockcatia.utils.skyblock.api;

import net.minecraft.util.EnumChatFormatting;

public class HypixelRank
{
    public enum Base
    {
        NONE("", EnumChatFormatting.GRAY),
        REGULAR("", EnumChatFormatting.GRAY),
        VIP("VIP", EnumChatFormatting.GREEN),
        VIP_PLUS("VIP", EnumChatFormatting.GREEN),
        MVP("MVP", EnumChatFormatting.AQUA),
        MVP_PLUS("MVP", EnumChatFormatting.AQUA);

        private final String name;
        private final EnumChatFormatting color;

        Base(String name, EnumChatFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public EnumChatFormatting getColor()
        {
            return this.color;
        }
    }

    public enum Type
    {
        NORMAL("", EnumChatFormatting.GRAY),
        ADMIN("ADMIN", EnumChatFormatting.RED),
        YOUTUBER("YOUTUBE", EnumChatFormatting.RED),
        HELPER("HELPER", EnumChatFormatting.BLUE),
        MODERATOR("MOD", EnumChatFormatting.DARK_GREEN);

        private final String name;
        private final EnumChatFormatting color;

        Type(String name, EnumChatFormatting color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public EnumChatFormatting getColor()
        {
            return this.color;
        }
    }
}