package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

@Mixin(EntityArmorStand.class)
public abstract class EntityArmorStandMixin extends EntityLivingBase
{
    @Shadow
    @Final
    private ItemStack[] contents;

    private EntityArmorStandMixin()
    {
        super(null);
    }

    @Inject(method = "interactAt(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/Vec3;)Z", cancellable = true, at = @At("HEAD"))
    private void interactAt(EntityPlayer player, Vec3 targetVec3, CallbackInfoReturnable info)
    {
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.ignoreInteractInvisibleArmorStand)
        {
            for (ItemStack content : this.contents)
            {
                if ((content == null || content != null && content.stackSize == 0) && this.isInvisible())
                {
                    info.setReturnValue(false);
                }
            }
        }
    }
}