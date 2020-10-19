package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(ArmorStandEntity.class)
public abstract class MixinArmorStandEntity extends LivingEntity
{
    private MixinArmorStandEntity()
    {
        super(null, null);
    }

    @Inject(method = "applyPlayerInteraction(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;", cancellable = true, at = @At("HEAD"))
    private void applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand, CallbackInfoReturnable<ActionResultType> info)
    {
        for (ItemStack content : this.getEquipmentAndArmor())
        {
            if (SkyBlockEventHandler.isSkyBlock && SBExtendedConfig.INSTANCE.ignoreInteractInvisibleArmorStand && (content.isEmpty() || !content.isEmpty() && content.getCount() == 0) && this.isInvisible())
            {
                info.setReturnValue(ActionResultType.PASS);
            }
        }
    }
}