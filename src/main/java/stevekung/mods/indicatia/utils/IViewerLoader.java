package stevekung.mods.indicatia.utils;

import net.minecraft.client.network.NetworkPlayerInfo;

public interface IViewerLoader
{
    public boolean isLoadedFromViewer();
    public NetworkPlayerInfo setLoadedFromViewer(boolean loaded);
}