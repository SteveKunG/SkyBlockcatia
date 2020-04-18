package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import stevekung.mods.indicatia.config.ConfigManagerIN;

@Mixin(LayerCape.class)
public abstract class LayerCapeMixin implements LayerRenderer<AbstractClientPlayer>
{
    @Override
    @Overwrite
    public boolean shouldCombineTextures()
    {
        return ConfigManagerIN.enableOldArmorRender;
    }
}