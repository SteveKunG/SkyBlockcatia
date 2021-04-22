package com.stevekung.skyblockcatia.event;

import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface GrapplingHookEvent
{
    Event<GrapplingHookEvent> GRAPPLING_HOOK = EventFactory.createLoop();
    void onHooked(ItemStack itemStack);
}