package com.stevekung.skyblockcatia.command.arguments;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.client.command.IClientSuggestionProvider;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.StringTextComponent;

public class SkyblockBazaarItemIdArgumentType implements ArgumentType<String>
{
    private static final DynamicCommandExceptionType ID_NOT_FOUND = new DynamicCommandExceptionType(obj -> new StringTextComponent(LangUtils.translate("commands.sbbazaar.id_not_found", obj)));
    private static final SimpleCommandExceptionType INVALID_ARGS = new SimpleCommandExceptionType(new StringTextComponent(LangUtils.translate("argument.id.invalid")));

    private SkyblockBazaarItemIdArgumentType() {}

    public static SkyblockBazaarItemIdArgumentType create()
    {
        return new SkyblockBazaarItemIdArgumentType();
    }

    public static String getItemId(CommandContext<IClientSuggestionProvider> context, String name)
    {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return SkyblockBazaarItemIdArgumentType.suggestIterable(MainEventHandler.BAZAAR_DATA.keySet(), builder);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        String id = SkyblockBazaarItemIdArgumentType.read(reader);

        if (MainEventHandler.BAZAAR_DATA.keySet().stream().anyMatch(product -> product.equals(id)))
        {
            return id;
        }
        else
        {
            throw SkyblockBazaarItemIdArgumentType.ID_NOT_FOUND.create(id);
        }
    }

    @Override
    public Collection<String> getExamples()
    {
        return Collections.singletonList("DIAMOND");
    }

    private static CompletableFuture<Suggestions> suggestIterable(Iterable<String> iterable, SuggestionsBuilder builder)
    {
        String typedString = builder.getRemaining().toLowerCase(Locale.ROOT);
        SkyblockBazaarItemIdArgumentType.applySuggest(iterable, typedString, string1 -> string1, builder::suggest);
        return builder.buildFuture();
    }

    private static void applySuggest(Iterable<String> iterable, String typedString, Function<String, String> function, Consumer<String> consumer)
    {
        for (String name : iterable)
        {
            String name2 = function.apply(name);

            if (name2.startsWith(typedString))
            {
                consumer.accept(name);
            }
        }
    }

    private static String read(StringReader reader) throws CommandSyntaxException
    {
        int cursor = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isValidPathCharacter(reader.peek()))
        {
            reader.skip();
        }

        String string = reader.getString().substring(cursor, reader.getCursor());

        try
        {
            return string;
        }
        catch (ResourceLocationException e)
        {
            reader.setCursor(cursor);
            throw SkyblockBazaarItemIdArgumentType.INVALID_ARGS.createWithContext(reader);
        }
    }
}