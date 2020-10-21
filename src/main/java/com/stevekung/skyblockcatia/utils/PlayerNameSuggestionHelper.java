package com.stevekung.skyblockcatia.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.ISuggestionProvider;

public class PlayerNameSuggestionHelper extends CommandSuggestionHelper
{
    public PlayerNameSuggestionHelper(Minecraft mc, Screen screen, TextFieldWidget textField, FontRenderer font, int maxAmountRendered)
    {
        super(mc, screen, textField, font, true, true, 0, maxAmountRendered, false, Integer.MIN_VALUE);
    }

    @Override
    public void init()
    {
        String text = this.inputField.getText();

        if (!this.isApplyingSuggestion)
        {
            this.inputField.setSuggestion(null);
            this.suggestions = null;
        }

        this.exceptionList.clear();
        int i = this.inputField.getCursorPosition();
        String s1 = text.substring(0, i);
        int k = CommandSuggestionHelper.getLastWhitespace(s1);
        Collection<String> collection = this.mc.player.connection.getSuggestionProvider().getPlayerNames().stream().filter(s -> !s.startsWith("!")).collect(Collectors.toList());
        this.suggestionsFuture = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
    }
}