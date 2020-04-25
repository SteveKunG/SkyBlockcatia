package com.stevekung.skyblockcatia.utils;

public class MinigameCommand
{
    private final String name;
    private final String command;
    private final boolean isMinigame;

    public MinigameCommand(String name, String command, boolean isMinigame)
    {
        this.name = name;
        this.command = command;
        this.isMinigame = isMinigame;
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
}