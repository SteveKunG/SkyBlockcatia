package com.stevekung.skyblockcatia.mixin;

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
import com.stevekung.skyblockcatia.utils.SkyBlockLocation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
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
        if (ExtendedConfig.instance.onlyMineableHitbox && HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION != SkyBlockLocation.YOUR_ISLAND)
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

    @Redirect(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    private MovingObjectPosition rayTraceBlocks(WorldClient world, Vec3 from, Vec3 to)
    {
        return world.rayTraceBlocks(from, to, false, true, true);
    }
}