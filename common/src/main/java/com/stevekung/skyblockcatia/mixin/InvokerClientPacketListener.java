package com.stevekung.skyblockcatia.mixin;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

@Mixin(ClientPacketListener.class)
public interface InvokerClientPacketListener
{
    @Accessor("playerInfoMap")
    Map<UUID, PlayerInfo> getPlayerInfoMap();
}