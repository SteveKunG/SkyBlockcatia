package com.stevekung.skyblockcatia.utils;

import com.mojang.authlib.GameProfile;
import com.stevekung.skyblockcatia.event.ClientEventHandler;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.world.World;

public class EntityOtherFakePlayer extends AbstractClientPlayer
{
    public EntityOtherFakePlayer(World world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public void onUpdate()
    {
        this.ticksExisted = ClientEventHandler.ticks;
    }
}