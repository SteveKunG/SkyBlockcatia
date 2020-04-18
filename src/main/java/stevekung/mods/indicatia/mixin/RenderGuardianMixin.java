package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderGuardian;
import net.minecraft.entity.monster.EntityGuardian;

@Mixin(RenderGuardian.class)
public abstract class RenderGuardianMixin
{
    @Inject(method = "doRender(Lnet/minecraft/entity/monster/EntityGuardian;DDDFF)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.popMatrix()V", shift = At.Shift.BEFORE))
    private void render(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info)
    {
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
    }
}