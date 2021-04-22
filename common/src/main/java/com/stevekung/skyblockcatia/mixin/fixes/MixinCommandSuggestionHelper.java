package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.stevekung.skyblockcatia.utils.Utils;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

@Mixin(CommandSuggestions.class)
public class MixinCommandSuggestionHelper
{
    @Redirect(method = "updateCommandInfo()V", at = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/ClientSuggestionProvider.getOnlinePlayerNames()Ljava/util/Collection;"))
    private Collection<String> getPlayerNames(ClientSuggestionProvider provider)
    {
        return Utils.filteredPlayers(provider.getOnlinePlayerNames());
    }
}