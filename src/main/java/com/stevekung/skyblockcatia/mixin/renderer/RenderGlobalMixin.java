package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.MinecraftForge;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin
{
    @Unique
    private IBlockState prevState;

    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "sendBlockBreakProgress(ILnet/minecraft/util/BlockPos;I)V", at = @At(value = "INVOKE", target = "java/util/Map.remove(Ljava/lang/Object;)Ljava/lang/Object;", remap = false))
    private void postServerBlockBreakProgress(int breakerId, BlockPos pos, int progress, CallbackInfo info)
    {
        IBlockState state = this.mc.theWorld.getBlockState(pos);

        if (breakerId == this.mc.thePlayer.getEntityId() && progress == -1)
        {
            this.prevState = state;
        }

        if (breakerId == 0 && progress == 10 && (state.getBlock() == Blocks.air || state.getBlock() == Blocks.bedrock) && !pos.equals(BlockPos.ORIGIN))
        {
            MinecraftForge.EVENT_BUS.post(new ClientBlockBreakEvent(this.mc.theWorld, pos, this.prevState));
            this.prevState = null;
        }
    }
}