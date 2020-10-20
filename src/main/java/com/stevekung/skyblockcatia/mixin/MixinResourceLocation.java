package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.ResourceLocation;

@Mixin(ResourceLocation.class)
public abstract class MixinResourceLocation
{
    @Inject(method = "isPathValid(Ljava/lang/String;)Z", cancellable = true, at = @At("RETURN"))
    private static void isPathValid(String path, CallbackInfoReturnable info)
    {
        if (path.equals("HYP|McVersion") || path.equals("LOLIMAHCKER")) // prevent error
        {
            info.setReturnValue(true);
        }
    }
}