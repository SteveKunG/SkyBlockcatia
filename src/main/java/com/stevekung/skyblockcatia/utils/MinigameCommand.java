package com.stevekung.skyblockcatia.utils;

public class MinigameCommand
{
    private final String name;
    private final String command;
    private final boolean isMinigame;
    private final String uuid;
    private final String texture;

    public MinigameCommand(String name, String command, boolean isMinigame, String uuid, String texture)
    {
        this.name = name;
        this.command = command;
        this.isMinigame = isMinigame;
        this.uuid = uuid;
        this.texture = texture;
    }

    public String getName()
    {
        return this.name;
    }

    public String getCommand()
    {
        return this.command;
    }

    public boolean isMinigame()
    {
        return this.isMinigame;
    }

    public String getUUID()
    {
        return this.uuid;
    }

    public String getTexture()
    {
        return this.texture;
    }
}