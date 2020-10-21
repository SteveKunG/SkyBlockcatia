package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class Utils
{
    public static boolean isHypixel()
    {
        ServerData server = Minecraft.getInstance().getCurrentServerData();
        return server != null && server.serverIP.contains("hypixel");
    }
}