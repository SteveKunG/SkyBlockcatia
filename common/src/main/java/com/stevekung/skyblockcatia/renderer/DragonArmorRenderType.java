package com.stevekung.skyblockcatia.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class DragonArmorRenderType extends RenderStateShard
{
    private DragonArmorRenderType(String name, Runnable setupTask, Runnable clearTask)
    {
        super(name, setupTask, clearTask);
    }

    public static RenderType getGlowingDragonOverlay(ResourceLocation location)
    {
        RenderStateShard.TextureStateShard textureState = new RenderStateShard.TextureStateShard(location, false, false);
        return RenderType.create("glowing_dragon_overlay", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(textureState).setTransparencyState(GLINT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setFogState(BLACK_FOG).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).createCompositeState(false));
    }
}