package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;

@Mixin(SocialInteractionsScreen.class)
public class MixinSocialInteractionsScreen
{
    @ModifyVariable(method = "updateServerLabel", at = @At(value = "INVOKE_ASSIGN", remap = false, target = "java/util/Collection.size()I", shift = Shift.AFTER))
    private int filterPlayer(int i)
    {
        return SkyBlockEventHandler.isSkyBlock ? (int)Minecraft.getInstance().getConnection().getOnlinePlayers().stream().filter(info -> !info.getProfile().getName().startsWith("!")).count() : i;
    }
}