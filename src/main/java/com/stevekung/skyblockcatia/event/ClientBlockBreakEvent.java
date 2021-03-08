package com.stevekung.skyblockcatia.event;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientBlockBreakEvent extends Event
{
    private final World world;
    private final BlockPos pos;
    @Nullable
    private final IBlockState prevState;

    public ClientBlockBreakEvent(World world, BlockPos pos)
    {
        this(world, pos, null);
    }

    public ClientBlockBreakEvent(World world, BlockPos pos, @Nullable IBlockState prevState)
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
    public IBlockState getBlockState()
    {
        return this.prevState;
    }
}