package com.stevekung.skyblockcatia.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GrapplingHookEvent extends Event
{
    private final ItemStack itemStack;

    public GrapplingHookEvent(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }
}