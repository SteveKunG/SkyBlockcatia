package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.SkyBlockLocation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

@Mixin(BlockFarmland.class)
public abstract class BlockFarmlandMixin extends Block
{
    public BlockFarmlandMixin(Material material)
    {
        super(material);
    }

    @Inject(method = "onFallenUpon(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/entity/Entity;F)V", cancellable = true, at = @At("HEAD"))
    private void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance, CallbackInfo info)
    {
        if (HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION != SkyBlockLocation.YOUR_ISLAND)
        {
            info.cancel();
        }
    }
}