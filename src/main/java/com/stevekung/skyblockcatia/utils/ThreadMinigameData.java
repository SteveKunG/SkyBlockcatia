package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;

import com.google.gson.*;

public class ThreadMinigameData extends Thread
{
    public ThreadMinigameData()
    {
        super("Minigame Data Thread");
    }

    @Override
    public void run()
    {
        try
        {
            URL url = new URL("https://raw.githubusercontent.com/SteveKunG/Indicatia/minigame_data/skyblock.json");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            JsonElement element = new JsonParser().parse(in);

            for (JsonElement minigameEle : element.getAsJsonArray())
            {
                JsonObject minigame = (JsonObject)minigameEle;
                String name = minigame.get("name").getAsString();
                boolean sort = !minigame.has("sort") ? true : minigame.get("sort").getAsBoolean();
                List<MinigameCommand> minigameCmds = new ArrayList<>();

                for (JsonElement commandEle : minigame.getAsJsonArray("commands"))
                {
                    JsonObject command = (JsonObject)commandEle;
                    String displayName = command.get("name").getAsString();
                    String minigameCommand = command.get("command").getAsString();
                    boolean isMinigame = command.get("minigame").getAsBoolean();
                    minigameCmds.add(new MinigameCommand(displayName, minigameCommand, isMinigame));
                    minigameCmds.sort((minigame1, minigame2) -> !sort ? 1 : new CompareToBuilder().append(minigame1.isMinigame(), minigame2.isMinigame()).append(minigame1.getName(), minigame2.getName()).build());
                }
                MinigameData.addMinigameData(new MinigameData(name, minigameCmds));
                MinigameData.getMinigameData().sort((minigame1, minigame2) -> minigame1.getName().equals("Main") ? -1 : new CompareToBuilder().append(minigame1.getName(), minigame2.getName()).build());
            }
            LoggerIN.info("Successfully getting minigames data from GitHub!");
        }
        catch (IOException | JsonIOException | JsonSyntaxException e)
        {
            e.printStackTrace();
            LoggerIN.error("Could not get minigames data from GitHub!");
            MinigameData.addMinigameData(new MinigameData("Could not get minigames data from Database!", new ArrayList<>()));
        }
    }
}