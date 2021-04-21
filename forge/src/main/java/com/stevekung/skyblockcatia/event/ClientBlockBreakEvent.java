package com.stevekung.skyblockcatia.event;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;

public class ClientBlockBreakEvent extends Event
{
    private final Level world;
    private final BlockPos pos;
    @Nullable
    private final BlockState prevState;

    public ClientBlockBreakEvent(Level world, BlockPos pos)
    {
        this(world, pos, null);
    }

    public ClientBlockBreakEvent(Level world, BlockPos pos, @Nullable BlockState prevState)
    {
        this.pos = pos;
        this.world = world;
        this.prevState = prevState;
    }

    public Level getWorld()
    {
        return this.world;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    @Nullable
    public BlockState getBlockState()
    {
        return this.prevState;
    }
}