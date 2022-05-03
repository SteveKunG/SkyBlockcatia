package com.stevekung.skyblockcatia.mixin.player_api.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.entity.EntityPlayerSP;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin
{
    @Redirect(method = "setPlayerSPHealth", remap = false, at = @At(value = "FIELD", remap = false, target = "Lnet/minecraft/client/entity/EntityPlayerSP;hurtTime:I"))
    private void setNoHurtTime(EntityPlayerSP entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}