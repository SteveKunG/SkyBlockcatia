package com.stevekung.skyblockcatia.mixin.player_api.entity;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin
{
    @Redirect(method = "localSetPlayerSPHealth(F)V", remap = false, at = @At(value = "FIELD", remap = false, target = "net/minecraft/client/entity/EntityPlayerSP.field_70737_aN:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}