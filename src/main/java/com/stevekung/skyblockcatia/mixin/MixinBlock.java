package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

@Mixin(Block.class)
public abstract class MixinBlock
{
    private final Block that = (Block) (Object) this;

    //TODO Test
    @Inject(method = "getCollisionShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;", cancellable = true, at = @At("HEAD"))
    private void collisionRayTrace(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> info)
    {
        if (SBExtendedConfig.INSTANCE.ignoreBushHitbox)
        {
            if (this.that instanceof FlowerBlock || this.that instanceof TallGrassBlock || this.that instanceof TallFlowerBlock || this.that instanceof MushroomBlock)
            {
                info.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}