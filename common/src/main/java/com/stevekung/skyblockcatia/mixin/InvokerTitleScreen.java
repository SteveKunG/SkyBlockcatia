package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.screens.TitleScreen;

@Mixin(TitleScreen.class)
public interface InvokerTitleScreen
{
    @Accessor("splash")
    void setSplash(String splash);
}