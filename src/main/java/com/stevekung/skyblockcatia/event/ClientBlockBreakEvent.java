package com.stevekung.skyblockcatia.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class ClientBlockBreakEvent extends Event
{
    private final World world;
    private final BlockPos pos;

    public ClientBlockBreakEvent(World world, BlockPos pos)
    {
        this.pos = pos;
        this.world = world;
    }

    public World getWorld()
    {
        return this.world;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }
}