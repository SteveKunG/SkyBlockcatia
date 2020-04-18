package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import stevekung.mods.indicatia.config.ConfigManagerIN;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
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

    @Redirect(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    private MovingObjectPosition rayTraceBlocks(WorldClient world, Vec3 from, Vec3 to)
    {
        return world.rayTraceBlocks(from, to, false, true, true);
    }
}