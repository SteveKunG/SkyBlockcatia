package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.extensions.IForgeItem;

@Mixin(Item.class)
public abstract class MixinItem implements IForgeItem
{
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        if (HypixelEventHandler.isSkyBlock && !newStack.isEmpty() && !oldStack.isEmpty() && (oldStack.getItem() == Items.BOW || oldStack.getItem() == Items.IRON_AXE) && oldStack.getItem() == newStack.getItem())
        {
            return false;
        }
        return !oldStack.equals(newStack);
    }
}