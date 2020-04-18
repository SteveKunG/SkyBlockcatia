package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import stevekung.mods.indicatia.event.HypixelEventHandler;

@Mixin(Item.class)
public abstract class ItemMixin
{
    @Inject(method = "shouldCauseReequipAnimation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z", cancellable = true, at = @At("HEAD"), remap = false)
    private void disableReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged, CallbackInfoReturnable info)
    {
        if (HypixelEventHandler.isSkyBlock && newStack != null && oldStack != null && (oldStack.getItem() == Items.bow || oldStack.getItem() == Items.iron_axe) && oldStack.getItem() == newStack.getItem())
        {
            info.setReturnValue(false);
        }
    }
}