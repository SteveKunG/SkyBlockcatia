package com.stevekung.skyblockcatia.event;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class ClientBlockBreakEvent extends Event
{
    private final World world;
    private final BlockPos pos;
    @Nullable
    private final BlockState prevState;

    public ClientBlockBreakEvent(World world, BlockPos pos)
    {
        this(world, pos, null);
    }

    public ClientBlockBreakEvent(World world, BlockPos pos, @Nullable BlockState prevState)
    {
        this.pos = pos;
        this.world = world;
        this.prevState = prevState;
    }

    public World getWorld()
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