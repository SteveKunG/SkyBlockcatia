package com.stevekung.skyblockcatia.mixin.entity.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.entity.player.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity
{
    @Redirect(method = "setPlayerSPHealth(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/entity/player/ClientPlayerEntity.hurtTime:I"))
    private void setNoHurtTime(ClientPlayerEntity entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}