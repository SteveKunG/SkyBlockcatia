package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.util.EnumFacing;
import stevekung.mods.indicatia.config.ConfigManagerIN;

@Mixin(TileEntitySkullRenderer.class)
public abstract class TileEntitySkullRendererMixin
{
    @Inject(method = "renderSkull(FFFLnet/minecraft/util/EnumFacing;FILcom/mojang/authlib/GameProfile;I)V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.enableAlpha()V", shift = At.Shift.AFTER))
    private void renderPre(float x, float y, float z, EnumFacing facing, float netHeadYaw, int skullType, GameProfile profile, int destroyStage, CallbackInfo info)
    {
        if (skullType == 3 && ConfigManagerIN.enableTransparentSkinRender)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
    }
}