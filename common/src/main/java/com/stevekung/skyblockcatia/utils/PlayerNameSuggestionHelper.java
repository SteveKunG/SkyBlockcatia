package com.stevekung.skyblockcatia.utils;

import java.util.Collection;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stevekung.skyblockcatia.mixin.InvokerCommandSuggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.SharedSuggestionProvider;

public class PlayerNameSuggestionHelper extends CommandSuggestions
{
    public PlayerNameSuggestionHelper(Minecraft mc, Screen screen, EditBox textField, Font font, int maxAmountRendered)
    {
        super(mc, screen, textField, font, true, true, 0, maxAmountRendered, false, Integer.MIN_VALUE);
    }

    @Override
    public void updateCommandInfo()
    {
        String text = ((InvokerCommandSuggestions)this).getInput().getValue();

        if (!((InvokerCommandSuggestions)this).isKeepSuggestions())
        {
            ((InvokerCommandSuggestions)this).getInput().setSuggestion(null);
            ((InvokerCommandSuggestions)this).setSuggestions(null);
        }

        ((InvokerCommandSuggestions)this).getCommandUsage().clear();
        int i = ((InvokerCommandSuggestions)this).getInput().getCursorPosition();
        String s1 = text.substring(0, i);
        int k = InvokerCommandSuggestions.invokeGetLastWordIndex(s1);
        Collection<String> collection = Utils.filteredPlayers(((InvokerCommandSuggestions)this).getMinecraft().player.connection.getSuggestionsProvider().getOnlinePlayerNames());
        ((InvokerCommandSuggestions)this).setPendingSuggestions(SharedSuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k)));
    }
}