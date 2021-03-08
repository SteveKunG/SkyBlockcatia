package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
    @Unique
    private BlockState prevState;

    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "sendBlockBreakProgress(ILnet/minecraft/util/math/BlockPos;I)V", at = @At(value = "INVOKE", target = "it/unimi/dsi/fastutil/ints/Int2ObjectMap.remove(I)Ljava/lang/Object;", remap = false))
    private void postServerBlockBreakProgress(int breakerId, BlockPos pos, int progress, CallbackInfo info)
    {
        BlockState state = this.mc.world.getBlockState(pos);

        if (breakerId == this.mc.player.getEntityId() && progress == -1)
        {
            this.prevState = state;
        }

        if (breakerId == 0 && progress == 10 && (state.getBlock().isAir(state, this.mc.world, pos) || state.getBlock() == Blocks.BEDROCK) && !pos.equals(BlockPos.ZERO))
        {
            MinecraftForge.EVENT_BUS.post(new ClientBlockBreakEvent(this.mc.world, pos, this.prevState));
            this.prevState = null;
        }
    }
}