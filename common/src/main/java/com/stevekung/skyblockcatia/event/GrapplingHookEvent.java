package com.stevekung.skyblockcatia.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.item.ItemStack;

public interface GrapplingHookEvent
{
    Event<GrapplingHookEvent> GRAPPLING_HOOK = EventFactory.createLoop();

    void onHooked(ItemStack itemStack);
}