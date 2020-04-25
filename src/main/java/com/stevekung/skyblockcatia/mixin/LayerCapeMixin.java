package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

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