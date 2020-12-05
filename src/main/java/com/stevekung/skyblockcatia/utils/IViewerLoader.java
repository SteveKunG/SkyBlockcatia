package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.network.play.NetworkPlayerInfo;

public interface IViewerLoader
{
    boolean isLoadedFromViewer();
    NetworkPlayerInfo setLoadedFromViewer(boolean loaded);
}