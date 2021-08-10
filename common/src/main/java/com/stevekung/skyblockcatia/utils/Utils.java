package com.stevekung.skyblockcatia.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;

public class Utils
{
    public static boolean isHypixel()
    {
        var server = Minecraft.getInstance().getCurrentServer();
        return server != null && server.ip.contains("hypixel");
    }

    public static Collection<String> filteredPlayers(Collection<String> collection)
    {
        return collection.stream().filter(s -> !s.startsWith("!")).collect(Collectors.toList());
    }
}