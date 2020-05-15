package com.stevekung.skyblockcatia.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.CommonUtils;

import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin
{
    private final EntityPlayerSP that = (EntityPlayerSP) (Object) this;

    @Inject(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "net/minecraft/util/MovementInput.updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateMovementInput(CallbackInfo info)
    {
        if (ConfigManagerIN.enableMovementHandler)
        {
            CommonUtils.onInputUpdate(this.that, this.that.movementInput);
        }
    }

    @Redirect(method = "setPlayerSPHealth(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.hurtTime:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = HypixelEventHandler.isSkyBlock ? 0 : oldValue;
    }
}