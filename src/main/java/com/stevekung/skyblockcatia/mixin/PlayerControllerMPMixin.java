package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.handler.ClientBlockBreakEvent;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin
{
    @Shadow
    @Final
    @Mutable
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", cancellable = true, at = @At("HEAD"))
    private void onPlayerDestroyBlock(BlockPos pos, EnumFacing facing, CallbackInfoReturnable info)
    {
        if (ExtendedConfig.instance.onlyMineableHitbox && HypixelEventHandler.isSkyBlock && !HypixelEventHandler.SKY_BLOCK_LOCATION.ignore())
        {
            IBlockState state = this.mc.theWorld.getBlockState(pos);

            if (!HypixelEventHandler.SKY_BLOCK_LOCATION.getMineableList().stream().anyMatch(block -> block.getMeta() == -1 ? state.getBlock() == block.getBlock() : state.getBlock() == block.getBlock() && state.getBlock().getMetaFromState(state) == block.getMeta()))
            {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "clickBlock(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", cancellable = true, at = @At("HEAD"))
    private void clickBlock(BlockPos pos, EnumFacing facing, CallbackInfoReturnable info)
    {
        if (ExtendedConfig.instance.onlyMineableHitbox && HypixelEventHandler.isSkyBlock && !HypixelEventHandler.SKY_BLOCK_LOCATION.ignore())
        {
            IBlockState state = this.mc.theWorld.getBlockState(pos);

            if (!HypixelEventHandler.SKY_BLOCK_LOCATION.getMineableList().stream().anyMatch(block -> block.getMeta() == -1 ? state.getBlock() == block.getBlock() : state.getBlock() == block.getBlock() && state.getBlock().getMetaFromState(state) == block.getMeta()))
            {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", at = @At(value = "INVOKE", target = "net/minecraft/block/Block.removedByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;Z)Z"))
    private void addBlockBreakEvent(BlockPos pos, EnumFacing facing, CallbackInfoReturnable info)
    {
        MinecraftForge.EVENT_BUS.post(new ClientBlockBreakEvent(this.mc.theWorld, pos));
    }
}