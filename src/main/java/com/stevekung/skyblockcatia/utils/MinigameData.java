package com.stevekung.skyblockcatia.utils;

import java.util.ArrayList;
import java.util.List;

public class MinigameData
{
    private static final List<MinigameData> DATA = new ArrayList<>();
    private final String name;
    private final List<MinigameCommand> commands;

    public MinigameData(String name, List<MinigameCommand> commands)
    {
        this.name = name;
        this.commands = commands;
    }

    public String getName()
    {
        return this.name;
    }

    public List<MinigameCommand> getCommands()
    {
        return this.commands;
    }

    public static List<MinigameData> getMinigameData()
    {
        return MinigameData.DATA;
    }

    public static void addMinigameData(MinigameData data)
    {
        MinigameData.DATA.add(data);
    }
}