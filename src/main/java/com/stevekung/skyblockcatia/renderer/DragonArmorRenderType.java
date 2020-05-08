package com.stevekung.skyblockcatia.renderer;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class DragonArmorRenderType extends RenderState
{
    private DragonArmorRenderType(String name, Runnable setupTask, Runnable clearTask)
    {
        super(name, setupTask, clearTask);
    }

    public static RenderType getGlowingDragonOverlay(ResourceLocation location)
    {
        RenderState.TextureState textureState = new RenderState.TextureState(location, false, false);
        return RenderType.makeType("glowing_dragon_overlay", DefaultVertexFormats.ENTITY, 7, 256, false, true, RenderType.State.getBuilder().texture(textureState).transparency(GLINT_TRANSPARENCY).writeMask(COLOR_WRITE).fog(BLACK_FOG).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).build(false));
    }
}