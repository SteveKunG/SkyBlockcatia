package com.stevekung.skyblockcatia.mixin.multiplayer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;

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
    private Minecraft mc;

    @Inject(method = "onPlayerDestroyBlock(Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", at = @At(value = "INVOKE", target = "net/minecraft/block/Block.removedByPlayer(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/entity/player/EntityPlayer;Z)Z"))
    private void addBlockBreakEvent(BlockPos pos, EnumFacing facing, CallbackInfoReturnable info)
    {
        MinecraftForge.EVENT_BUS.post(new ClientBlockBreakEvent(this.mc.theWorld, pos));
    }
}