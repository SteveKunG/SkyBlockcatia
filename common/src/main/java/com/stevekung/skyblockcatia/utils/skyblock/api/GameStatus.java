package com.stevekung.skyblockcatia.utils.skyblock.api;

public record GameStatus(Session session)
{
    public record Session(boolean online, String gameType, String mode) {}
}