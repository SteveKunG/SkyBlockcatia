package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.block.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

@Mixin(Block.class)
public class BlockMixin
{
    private final Block that = (Block) (Object) this;

    @Inject(method = "collisionRayTrace(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;", cancellable = true, at = @At("HEAD"))
    private void collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end, CallbackInfoReturnable<MovingObjectPosition> info)
    {
        if (HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.ignoreBushHitbox)
        {
            if (this.that instanceof BlockFlower || this.that instanceof BlockTallGrass || this.that instanceof BlockDoublePlant || this.that instanceof BlockMushroom)
            {
                info.setReturnValue(null);
            }
        }
    }
}