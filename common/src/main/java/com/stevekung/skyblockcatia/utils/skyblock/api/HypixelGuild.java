package com.stevekung.skyblockcatia.utils.skyblock.api;

public record HypixelGuild(Guild guild)
{
    public record Guild(String name) {}
}