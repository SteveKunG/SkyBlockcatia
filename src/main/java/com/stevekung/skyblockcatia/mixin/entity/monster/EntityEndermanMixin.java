package com.stevekung.skyblockcatia.mixin.entity.monster;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.monster.EntityEnderman;

@Mixin(EntityEnderman.class)
public class EntityEndermanMixin
{
    @Inject(method = "teleportRandomly()Z", cancellable = true, at = @At("HEAD"))
    private void teleportRandomly(CallbackInfoReturnable<Boolean> info)
    {
        if (((EntityEnderman)(Object)this).worldObj.isRemote)
        {
            info.setReturnValue(false);
        }
    }
}