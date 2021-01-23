package com.stevekung.skyblockcatia.mixin.entity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.CommonUtils;

import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin
{
    private final EntityPlayerSP that = (EntityPlayerSP) (Object) this;

    @Inject(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.isUsingItem()Z", shift = At.Shift.BEFORE))
    private void updateMovementInput(CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enableMovementHandler)
        {
            CommonUtils.onInputUpdate(this.that, this.that.movementInput);
        }
    }

    @Redirect(method = "setPlayerSPHealth(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.hurtTime:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}