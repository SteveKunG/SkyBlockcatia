package com.stevekung.skyblockcatia.utils.skyblock;

import com.mojang.authlib.GameProfile;
import com.stevekung.stevekungslib.client.event.ClientEventHandler;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class SBFakePlayerEntity extends AbstractClientPlayerEntity
{
    public SBFakePlayerEntity(ClientWorld world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public void tick()
    {
        this.ticksExisted = ClientEventHandler.ticks;
    }
}