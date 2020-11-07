package com.stevekung.skyblockcatia.mixin;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.ClientUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;
import net.minecraft.world.World;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @Shadow
    private Minecraft mc;

    @Inject(method = "renderHand(FI)V", at = @At("HEAD"))
    private void renderPre(float partialTicks, int xOffset, CallbackInfo info)
    {
        if (ConfigManagerIN.enableTransparentSkinRender)
        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }
    }

    @Inject(method = "renderHand(FI)V", at = @At("RETURN"))
    private void renderPost(float partialTicks, int xOffset, CallbackInfo info)
    {
        if (ConfigManagerIN.enableTransparentSkinRender)
        {
            GlStateManager.disableBlend();
        }
    }

    @Inject(method = "hurtCameraEffect(F)V", cancellable = true, at = @At("HEAD"))
    private void hurtCameraEffect(float partialTicks, CallbackInfo info)
    {
        if (ConfigManagerIN.disableHurtCameraEffect)
        {
            info.cancel();
        }
    }

    @Inject(method = "isDrawBlockOutline()Z", cancellable = true, at = @At("HEAD"))
    private void isDrawBlockOutline(CallbackInfoReturnable info)
    {
        if (ExtendedConfig.instance.onlyMineableHitbox && HypixelEventHandler.isSkyBlock && !HypixelEventHandler.SKY_BLOCK_LOCATION.ignore() && !ClientUtils.isControlKeyDown())
        {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                IBlockState state = this.mc.theWorld.getBlockState(this.mc.objectMouseOver.getBlockPos());

                if (!HypixelEventHandler.SKY_BLOCK_LOCATION.getMineableList().stream().anyMatch(block -> block.getMeta() == -1 ? state.getBlock() == block.getBlock() : state.getBlock() == block.getBlock() && state.getBlock().getMetaFromState(state) == block.getMeta()))
                {
                    info.setReturnValue(false);
                }
            }
        }
    }

    @Redirect(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/WorldClient.rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    private MovingObjectPosition rayTraceBlocks(WorldClient world, Vec3 from, Vec3 to)
    {
        return this.rayTraceBlocks(world, from, to, false, true, true);
    }

    private MovingObjectPosition rayTraceBlocks(World world, Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord))
        {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord))
            {
                int i = MathHelper.floor_double(vec32.xCoord);
                int j = MathHelper.floor_double(vec32.yCoord);
                int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(world, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid))
                {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, blockpos, vec31, vec32);

                    if (movingobjectposition != null)
                    {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition2 = null;
                int k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))
                    {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if (k > j1)
                    {
                        d2 = j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;

                    if (flag2)
                    {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if (flag)
                    {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if (flag1)
                    {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if ((!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(world, blockpos, iblockstate1) != null) && !(block1 instanceof BlockGlass || block1 instanceof BlockStainedGlass || block1 instanceof BlockPane))
                    {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid))
                        {
                            MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if (movingobjectposition1 != null)
                            {
                                return movingobjectposition1;
                            }
                        }
                        else
                        {
                            movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}