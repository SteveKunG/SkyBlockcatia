package com.stevekung.skyblockcatia.mixin.gui.screens.inventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu>
{
    MixinInventoryScreen()
    {
        super(null, null, null);
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screens/inventory/InventoryScreen.addButton(Lnet/minecraft/client/gui/components/AbstractWidget;)Lnet/minecraft/client/gui/components/AbstractWidget;"))
    private <T extends AbstractWidget> T disableRecipeBook(InventoryScreen screen, T widget)
    {
        return SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory ? widget : this.addButton(widget);
    }
}