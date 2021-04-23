package com.stevekung.skyblockcatia.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import net.minecraft.SharedConstants;

@Mixin(SharedConstants.class)
public class MixinSharedConstants
{
    @Inject(method = "isAllowedChatCharacter", cancellable = true, at = @At("HEAD"))
    private static void debugSectionSign(char character, CallbackInfoReturnable<Boolean> info)
    {
        if (GameProfileUtils.isSteveKunG())
        {
            info.setReturnValue(character >= 32 && character != 127);
        }
    }
}