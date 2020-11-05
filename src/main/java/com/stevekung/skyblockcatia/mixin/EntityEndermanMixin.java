package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;

@Mixin(EntityEnderman.class)
public class EntityEndermanMixin extends EntityMob
{
    private EntityEndermanMixin()
    {
        super(null);
    }

    @Inject(method = "teleportRandomly()Z", cancellable = true, at = @At("HEAD"))
    private void teleportRandomly(CallbackInfoReturnable info)
    {
        if (this.worldObj.isRemote)
        {
            info.setReturnValue(false);
        }
    }
}