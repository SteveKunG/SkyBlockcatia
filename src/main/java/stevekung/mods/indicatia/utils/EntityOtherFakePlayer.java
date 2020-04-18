package stevekung.mods.indicatia.utils;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.world.World;
import stevekung.mods.indicatia.event.ClientEventHandler;

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