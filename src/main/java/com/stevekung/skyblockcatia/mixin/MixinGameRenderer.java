package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer
{
    @Shadow
    @Final
    @Mutable
    private Minecraft mc;

    @Inject(method = "hurtCameraEffect(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", cancellable = true, at = @At("HEAD"))
    private void hurtCameraEffect(MatrixStack stack, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.GENERAL.disableHurtCameraEffect.get())
        {
            info.cancel();
        }
    }

    @Inject(method = "isDrawBlockOutline()Z", cancellable = true, at = @At("HEAD"))
    private void isDrawBlockOutline(CallbackInfoReturnable info)
    {
        if (SBExtendedConfig.INSTANCE.onlyMineableHitbox && SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION != SBLocation.YOUR_ISLAND)
        {
            RayTraceResult raytraceresult = this.mc.objectMouseOver;

            if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
                BlockState blockstate = this.mc.world.getBlockState(blockpos);

                if (!SkyBlockEventHandler.SKY_BLOCK_LOCATION.getMineableList().stream().anyMatch(block -> blockstate.getBlock() == block.getBlock()))
                {
                    info.setReturnValue(false);
                }
            }
        }
    }
}