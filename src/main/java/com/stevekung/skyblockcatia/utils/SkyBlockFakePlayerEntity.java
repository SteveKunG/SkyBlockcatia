package com.stevekung.skyblockcatia.utils;

import com.mojang.authlib.GameProfile;
import com.stevekung.stevekungslib.client.event.ClientEventHandler;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class SkyBlockFakePlayerEntity extends AbstractClientPlayerEntity
{
    public SkyBlockFakePlayerEntity(ClientWorld world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public void tick()
    {
        this.ticksExisted = ClientEventHandler.ticks;
    }
}