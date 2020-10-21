package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SSetPassengersPacket;

@Mixin(ClientPlayNetHandler.class)
public abstract class MixinClientPlayNetHandler
{
    @Shadow
    private ClientWorld world;

    @Inject(method = "handleSetPassengers(Lnet/minecraft/network/play/server/SSetPassengersPacket;)V", cancellable = true, at = @At("HEAD"))
    private void handleSetPassengers(SSetPassengersPacket packet, CallbackInfo info)
    {
        Entity entity = this.world.getEntityByID(packet.getEntityId());

        if (Utils.isHypixel() && entity == null)
        {
            info.cancel();
        } 
    }
}