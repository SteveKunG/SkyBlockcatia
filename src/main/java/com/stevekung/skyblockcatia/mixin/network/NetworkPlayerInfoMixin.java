package com.stevekung.skyblockcatia.mixin.network;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.utils.IViewerLoader;

import net.minecraft.client.network.NetworkPlayerInfo;

@Mixin(NetworkPlayerInfo.class)
public class NetworkPlayerInfoMixin implements IViewerLoader
{
    private final NetworkPlayerInfo that = (NetworkPlayerInfo) (Object) this;
    private boolean loadedFromViewer;

    @Override
    public boolean isLoadedFromViewer()
    {
        return this.loadedFromViewer;
    }

    @Override
    public NetworkPlayerInfo setLoadedFromViewer(boolean loaded)
    {
        this.loadedFromViewer = loaded;
        return this.that;
    }
}