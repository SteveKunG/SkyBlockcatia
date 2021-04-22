package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.multiplayer.PlayerInfo;

public interface IViewerLoader
{
    boolean isLoadedFromViewer();
    PlayerInfo setLoadedFromViewer(boolean loaded);
}