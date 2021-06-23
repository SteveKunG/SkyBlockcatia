package com.stevekung.skyblockcatia.mixin.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer
{
    @ModifyConstant(method = "hurtTo(F)V", constant = @Constant(intValue = 10, ordinal = 1))
    private int noHurt(int defValue)
    {
        return SkyBlockEventHandler.isSkyBlock ? 0 : defValue;
    }
}