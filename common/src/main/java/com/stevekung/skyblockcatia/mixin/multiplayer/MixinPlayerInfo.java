package com.stevekung.skyblockcatia.mixin.multiplayer;

import org.spongepowered.asm.mixin.Mixin;
import com.stevekung.skyblockcatia.utils.IViewerLoader;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo implements IViewerLoader
{
    private boolean loadedFromViewer;

    @Override
    public boolean isLoadedFromViewer()
    {
        return this.loadedFromViewer;
    }

    @Override
    public PlayerInfo setLoadedFromViewer(boolean loaded)
    {
        this.loadedFromViewer = loaded;
        return (PlayerInfo) (Object) this;
    }
}