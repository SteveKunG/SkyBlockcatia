package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.screens.social.SocialInteractionsPlayerList;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(SocialInteractionsPlayerList.class)
public class MixinSocialInteractionsPlayerList
{
    @ModifyVariable(method = "updatePlayerList(Ljava/util/Collection;D)V", index = 6, at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/client/multiplayer/ClientPacketListener.getPlayerInfo(Ljava/util/UUID;)Lnet/minecraft/client/multiplayer/PlayerInfo;", shift = Shift.AFTER))
    private PlayerInfo filterInvalidPlayer(PlayerInfo networkplayerinfo)
    {
        return networkplayerinfo.getProfile().getName().startsWith("!") ? null : networkplayerinfo;
    }

    @Inject(method = "addPlayer(Lnet/minecraft/client/multiplayer/PlayerInfo;Lnet/minecraft/client/gui/screens/social/SocialInteractionsScreen$Page;)V", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screens/social/SocialInteractionsPlayerList.addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I", shift = Shift.BEFORE))
    private void filterPlayer(PlayerInfo playerInfo, SocialInteractionsScreen.Page mode, CallbackInfo info)
    {
        if (playerInfo.getProfile().getName().startsWith("!"))
        {
            info.cancel();
        }
    }
}