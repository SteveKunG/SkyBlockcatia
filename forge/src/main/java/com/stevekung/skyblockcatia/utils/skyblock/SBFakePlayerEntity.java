package com.stevekung.skyblockcatia.utils.skyblock;

import com.mojang.authlib.GameProfile;
import com.stevekung.stevekungslib.proxy.LibClientProxy;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;

public class SBFakePlayerEntity extends AbstractClientPlayer
{
    public SBFakePlayerEntity(ClientLevel world, GameProfile profile)
    {
        super(world, profile);
    }

    @Override
    public void tick()
    {
        this.tickCount = LibClientProxy.ticks;
    }
}