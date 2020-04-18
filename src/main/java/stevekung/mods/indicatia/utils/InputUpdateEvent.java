package stevekung.mods.indicatia.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class InputUpdateEvent extends PlayerEvent
{
    private final MovementInput movementInput;

    public InputUpdateEvent(EntityPlayer player, MovementInput movementInput)
    {
        super(player);
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput()
    {
        return this.movementInput;
    }

    public EntityPlayer getEntityPlayer()
    {
        return this.entityPlayer;
    }
}