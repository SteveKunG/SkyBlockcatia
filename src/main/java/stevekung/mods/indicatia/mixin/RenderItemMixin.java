package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import stevekung.mods.indicatia.config.ConfigManagerIN;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin
{
    @Redirect(method = "renderItemModelForEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;)V", at = @At(value = "NEW", target = "net/minecraft/client/resources/model/ModelResourceLocation", ordinal = 0))
    private ModelResourceLocation setNewFishingRodModel(String p_i46081_1_, String p_i46081_2_)
    {
        return ConfigManagerIN.enableOldFishingRodRenderModel ? new ModelResourceLocation("indicatia:fishing_rod_cast", "inventory") : new ModelResourceLocation(p_i46081_1_, p_i46081_2_);
    }
}