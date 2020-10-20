package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlock_AbstractBlockState
{
    private final AbstractBlock.AbstractBlockState that = (AbstractBlock.AbstractBlockState) (Object) this;

    @Inject(method = "getShape(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/shapes/ISelectionContext;)Lnet/minecraft/util/math/shapes/VoxelShape;", cancellable = true, at = @At("HEAD"))
    private void getShape(IBlockReader world, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.ignoreBushHitbox && SkyBlockEventHandler.isSkyBlock)
        {
            if (this.that.getBlock() instanceof FlowerBlock || this.that.getBlock() instanceof TallGrassBlock || this.that.getBlock() instanceof TallFlowerBlock || this.that.getBlock() instanceof MushroomBlock)
            {
                info.setReturnValue(VoxelShapes.empty());
            }
        }
    }
}