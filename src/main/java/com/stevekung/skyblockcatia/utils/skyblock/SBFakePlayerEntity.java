package com.stevekung.skyblockcatia.utils.skyblock;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.event.handler.ClientEventHandler;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.world.World;

public class SBFakePlayerEntity extends AbstractClientPlayer
{
    public SBFakePlayerEntity(World world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public void onUpdate()
    {
        this.ticksExisted = ClientEventHandler.ticks;
    }
}