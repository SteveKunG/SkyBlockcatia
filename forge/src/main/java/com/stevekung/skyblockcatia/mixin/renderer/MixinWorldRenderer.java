package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

@Mixin(LevelRenderer.class)
public class MixinWorldRenderer
{
    @Unique
    private BlockState prevState;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "destroyBlockProgress", at = @At(value = "INVOKE", target = "it/unimi/dsi/fastutil/ints/Int2ObjectMap.remove(I)Ljava/lang/Object;", remap = false))
    private void postServerBlockBreakProgress(int breakerId, BlockPos pos, int progress, CallbackInfo info)
    {
        BlockState state = this.minecraft.level.getBlockState(pos);

        if (breakerId == this.minecraft.player.getId() && progress == -1)
        {
            this.prevState = state;
        }

        if (breakerId == 0 && progress == 10 && (state.getBlock().isAir(state, this.minecraft.level, pos) || state.getBlock() == Blocks.BEDROCK) && !pos.equals(BlockPos.ZERO))
        {
            MinecraftForge.EVENT_BUS.post(new ClientBlockBreakEvent(this.minecraft.level, pos, this.prevState));
            this.prevState = null;
        }
    }
}