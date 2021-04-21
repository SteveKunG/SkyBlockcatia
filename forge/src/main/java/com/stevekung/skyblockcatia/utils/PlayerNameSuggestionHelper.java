package com.stevekung.skyblockcatia.utils;

import java.util.Collection;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
        String text = this.input.getValue();

        if (!this.keepSuggestions)
        {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }

        this.commandUsage.clear();
        int i = this.input.getCursorPosition();
        String s1 = text.substring(0, i);
        int k = CommandSuggestions.getLastWordIndex(s1);
        Collection<String> collection = Utils.filteredPlayers(this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames());
        this.pendingSuggestions = SharedSuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
    }
}