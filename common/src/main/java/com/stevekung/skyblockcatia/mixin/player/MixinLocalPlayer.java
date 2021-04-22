package com.stevekung.skyblockcatia.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer
{
    @Redirect(method = "hurtTo(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/player/LocalPlayer.hurtTime:I"))
    private void setNoHurtTime(LocalPlayer entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}