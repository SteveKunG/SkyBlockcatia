package com.stevekung.skyblockcatia.mixin.fabric.gui.screens.inventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import com.mojang.blaze3d.platform.InputConstants;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen
{
    @Redirect(method = "checkHotbarKeyPressed(II)Z", slice = @Slice(from = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty()Z"), to = @At(value = "CONSTANT", args = "intValue=40")), at = @At(value = "INVOKE", remap = false, target = "net/minecraft/client/KeyMapping.matches(II)Z"))
    private boolean disableItemStackSwap(KeyMapping key, int i, int j)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.matches(i, j);
    }
}