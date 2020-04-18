package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import stevekung.mods.indicatia.utils.EntityOtherFakePlayer;

@Mixin(RendererLivingEntity.class)
public abstract class RendererLivingEntityMixin extends Render<EntityLivingBase>
{
    protected RendererLivingEntityMixin(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Inject(method = "canRenderName(Lnet/minecraft/entity/EntityLivingBase;)Z", cancellable = true, at = @At("HEAD"))
    private void renderName(EntityLivingBase entity, CallbackInfoReturnable info)
    {
        if (entity instanceof EntityOtherFakePlayer)
        {
            info.setReturnValue(false);
        }
    }

    @Redirect(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.rotate(FFFF)V", ordinal = 1))
    private void rotate(float angle, float x, float y, float z)
    {
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}