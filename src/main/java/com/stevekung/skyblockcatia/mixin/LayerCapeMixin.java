package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;

import net.minecraft.client.renderer.entity.layers.LayerCape;

@Mixin(LayerCape.class)
public abstract class LayerCapeMixin
{
    @Overwrite
    public boolean shouldCombineTextures()
    {
        return ConfigManagerIN.enableOldArmorRender;
    }
}