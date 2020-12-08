package com.stevekung.skyblockcatia.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.inventory.GuiContainer;

@Mixin(GuiContainer.class)
public interface GuiContainerMixin
{
    @Accessor
    int getGuiTop();
}