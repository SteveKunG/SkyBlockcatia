package com.stevekung.skyblockcatia.mixin.renderer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    @Shadow
    @Final
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
        if (SkyBlockcatiaSettings.INSTANCE.onlyMineableHitbox && SkyBlockEventHandler.isSkyBlock && !SkyBlockEventHandler.SKY_BLOCK_LOCATION.ignore() && !ClientUtils.isControlKeyDown())
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

    @Inject(method = "getNightVisionBrightness(Lnet/minecraft/entity/LivingEntity;F)F", cancellable = true, at = @At("HEAD"))
    private static void getNightVisionBrightness(LivingEntity entity, float partialTicks, CallbackInfoReturnable info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.disableNightVision)
        {
            info.setReturnValue(0.0F);
        }
    }
}