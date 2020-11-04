package com.stevekung.skyblockcatia.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class Utils
{
    public static boolean isHypixel()
    {
        ServerData server = Minecraft.getInstance().getCurrentServerData();
        return server != null && server.serverIP.contains("hypixel");
    }

    public static Collection<String> filteredPlayers(Collection<String> collection)
    {
        return collection.stream().filter(s -> !s.startsWith("!")).collect(Collectors.toList());
    }
}