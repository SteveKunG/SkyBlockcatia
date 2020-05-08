package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.network.play.NetworkPlayerInfo;

public interface IViewerLoader
{
    public boolean isLoadedFromViewer();
    public NetworkPlayerInfo setLoadedFromViewer(boolean loaded);
}