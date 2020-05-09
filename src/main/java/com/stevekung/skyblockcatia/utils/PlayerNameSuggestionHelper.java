package com.stevekung.skyblockcatia.utils;

import java.util.Collection;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.ISuggestionProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerNameSuggestionHelper extends CommandSuggestionHelper
{
    public PlayerNameSuggestionHelper(Minecraft mc, Screen screen, TextFieldWidget textField, FontRenderer font, boolean p_i225919_5_, boolean p_i225919_6_, int p_i225919_7_, int p_i225919_8_, boolean p_i225919_9_, int p_i225919_10_)
    {
        super(mc, screen, textField, font, p_i225919_5_, p_i225919_6_, p_i225919_7_, p_i225919_8_, p_i225919_9_, p_i225919_10_);
    }

    @Override
    public void init()
    {
        String text = this.field_228095_d_.getText();

        if (!this.field_228110_s_)
        {
            this.field_228095_d_.setSuggestion(null);
            this.field_228108_q_ = null;
        }

        this.field_228103_l_.clear();
        int i = this.field_228095_d_.getCursorPosition();
        String s1 = text.substring(0, i);
        int k = CommandSuggestionHelper.func_228121_a_(s1);
        Collection<String> collection = this.field_228093_b_.player.connection.getSuggestionProvider().getPlayerNames();
        this.field_228107_p_ = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
    }
}