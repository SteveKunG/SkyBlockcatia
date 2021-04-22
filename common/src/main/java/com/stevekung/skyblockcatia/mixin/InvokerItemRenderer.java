package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.entity.ItemRenderer;

@Mixin(ItemRenderer.class)
public interface InvokerItemRenderer
{
    @Invoker
    void invokeFillRect(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p);
}