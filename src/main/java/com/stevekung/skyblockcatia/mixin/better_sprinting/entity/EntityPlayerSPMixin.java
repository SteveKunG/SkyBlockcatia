package com.stevekung.skyblockcatia.mixin.better_sprinting.entity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin
{
    @Redirect(method = "setPlayerSPHealth(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/entity/EntityPlayerSP.hurtTime:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}