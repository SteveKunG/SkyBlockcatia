package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.network.NetworkPlayerInfo;
import stevekung.mods.indicatia.utils.IViewerLoader;

@Mixin(NetworkPlayerInfo.class)
public abstract class NetworkPlayerInfoMixin implements IViewerLoader
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