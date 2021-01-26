package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.social.SocialInteractionsScreen;

@Mixin(SocialInteractionsScreen.class)
public class MixinSocialInteractionsScreen
{
    @ModifyVariable(method = "func_244680_a(Lnet/minecraft/client/Minecraft;)V", at = @At(value = "INVOKE_ASSIGN", remap = false, target = "java/util/Collection.size()I", shift = Shift.AFTER))
    private int filterPlayer(int i)
    {
        return SkyBlockEventHandler.isSkyBlock ? Minecraft.getInstance().getConnection().getPlayerInfoMap().stream().filter(info -> !info.getGameProfile().getName().startsWith("!")).collect(Collectors.toList()).size() : i;
    }
}