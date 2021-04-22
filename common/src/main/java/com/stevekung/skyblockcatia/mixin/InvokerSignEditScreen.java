package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;

@Mixin(SignEditScreen.class)
public interface InvokerSignEditScreen
{
    @Accessor("signField")
    TextFieldHelper getSignField();

    @Accessor("sign")
    SignBlockEntity getSign();
}
