package com.stevekung.skyblockcatia.mixin.player_api;

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

    @Inject(method = "localOnLivingUpdate()V", remap = false, at = @At(value = "INVOKE", remap = false, target = "net/minecraft/util/MovementInput.func_78898_a()V", shift = At.Shift.AFTER))
    private void updateMovementInput(CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.enableMovementHandler)
        {
            CommonUtils.onInputUpdate(this.that, this.that.movementInput);
        }
    }

    @Redirect(method = "localSetPlayerSPHealth(F)V", remap = false, at = @At(value = "FIELD", remap = false, target = "net/minecraft/client/entity/EntityPlayerSP.field_70737_aN:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}