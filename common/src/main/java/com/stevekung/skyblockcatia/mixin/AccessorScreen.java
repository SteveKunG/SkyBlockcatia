package com.stevekung.skyblockcatia.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public interface AccessorScreen
{
    @Accessor("buttons")
    List<AbstractWidget> getButtons();

    @Invoker
    <T extends AbstractWidget> T invokeAddButton(T abstractWidget);
}