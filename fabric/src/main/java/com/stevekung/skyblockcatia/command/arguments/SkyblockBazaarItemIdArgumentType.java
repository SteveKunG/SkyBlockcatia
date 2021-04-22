package com.stevekung.skyblockcatia.command.arguments;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;

public class SkyblockBazaarItemIdArgumentType implements ArgumentType<String>
{
    private SkyblockBazaarItemIdArgumentType() {}

    public static SkyblockBazaarItemIdArgumentType create()
    {
        return new SkyblockBazaarItemIdArgumentType();
    }

    public static String getItemId(CommandContext<FabricClientCommandSource> context, String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return SharedSuggestionProvider.suggest(MainEventHandler.BAZAAR_DATA.keySet(), builder);
    }

    @Override
    public String parse(StringReader reader)
    {
        return reader.readUnquotedString();
    }

    @Override
    public Collection<String> getExamples()
    {
        return Collections.singletonList("DIAMOND");
    }
}