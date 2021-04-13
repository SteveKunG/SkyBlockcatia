package com.stevekung.skyblockcatia.mixin.network.play;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.utils.IViewerLoader;

import net.minecraft.client.network.play.NetworkPlayerInfo;

@Mixin(NetworkPlayerInfo.class)
public class MixinNetworkPlayerInfo implements IViewerLoader
{
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
        return (NetworkPlayerInfo)(Object)this;
    }
}