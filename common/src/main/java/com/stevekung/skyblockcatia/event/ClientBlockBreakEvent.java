package com.stevekung.skyblockcatia.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ClientBlockBreakEvent
{
    Event<ClientBlockBreakEvent> CLIENT_BLOCK_BREAK = EventFactory.createLoop();
    void onBlockBreak(Level level, BlockPos pos, BlockState prevState);
}