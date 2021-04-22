package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;

@Mixin(CommandSuggestions.class)
public interface InvokerCommandSuggestions
{
    @Accessor("suggestions")
    CommandSuggestions.SuggestionsList getSuggestions();

    @Accessor("input")
    EditBox getInput();

    @Accessor("keepSuggestions")
    boolean isKeepSuggestions();

    @Accessor("commandUsage")
    List<FormattedCharSequence> getCommandUsage();

    @Accessor("pendingSuggestions")
    void setPendingSuggestions(CompletableFuture<Suggestions> suggestions);

    @Accessor("minecraft")
    Minecraft getMinecraft();

    @Accessor("suggestions")
    void setSuggestions(CommandSuggestions.SuggestionsList list);

    @Invoker
    static int invokeGetLastWordIndex(String string)
    {
        throw new Error();
    }
}