package com.stevekung.skyblockcatia.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.utils.GameProfileUtils;

import net.minecraft.util.ChatAllowedCharacters;

@Mixin(ChatAllowedCharacters.class)
public class ChatAllowedCharactersMixin
{
    @Inject(method = "isAllowedCharacter(C)Z", cancellable = true, at = @At("HEAD"))
    private static void isAllowedCharacter(char character, CallbackInfoReturnable info)
    {
        if (GameProfileUtils.isSteveKunG())
        {
            info.setReturnValue(character == 167 || character >= 32 && character != 127);
        }
    }
}