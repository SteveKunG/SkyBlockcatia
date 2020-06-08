package com.stevekung.skyblockcatia.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui
{
    private static final List<String> IGNORE_TOOLTIPS = new ArrayList<>(Arrays.asList(" "));

    @Inject(method = "renderToolTip(Lnet/minecraft/item/ItemStack;II)V", cancellable = true, at = @At("HEAD"))
    private void renderToolTip(ItemStack itemStack, int x, int y, CallbackInfo info)
    {
        if (this.ignoreNullItem(itemStack, IGNORE_TOOLTIPS))
        {
            info.cancel();
        }
    }

    private boolean ignoreNullItem(ItemStack itemStack, List<String> ignores)
    {
        String displayName = EnumChatFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName());
        return ignores.stream().anyMatch(name -> displayName.equals(name));
    }
}