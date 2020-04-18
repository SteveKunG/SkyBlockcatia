package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

@Mixin(EntityEnderman.class)
public abstract class EntityEndermanMixin extends EntityMob
{
    public EntityEndermanMixin(World world)
    {
        super(world);
    }

    @Inject(method = "teleportRandomly()Z", cancellable = true, at = @At("HEAD"))
    private void teleportRandomly(CallbackInfoReturnable info)
    {
        if (this.worldObj.isRemote)
        {
            info.setReturnValue(false);
        }
    }
}