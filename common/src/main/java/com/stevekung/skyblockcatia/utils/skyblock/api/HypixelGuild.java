package com.stevekung.skyblockcatia.utils.skyblock.api;

public class HypixelGuild
{
    private final Guild guild;

    public HypixelGuild(Guild guild)
    {
        this.guild = guild;
    }

    public Guild getGuild()
    {
        return this.guild;
    }

    public static class Guild
    {
        private final String name;

        public Guild(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}