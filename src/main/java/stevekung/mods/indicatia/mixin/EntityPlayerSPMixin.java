package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import stevekung.mods.indicatia.utils.CommonUtils;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends AbstractClientPlayer
{
    private final EntityPlayerSP that = (EntityPlayerSP) (Object) this;

    @Shadow
    private boolean hasValidHealth;

    public EntityPlayerSPMixin(World world, GameProfile profile)
    {
        super(world, profile);
    }

    @Inject(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "net/minecraft/util/MovementInput.updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void updateMovementInput(CallbackInfo info)
    {
        CommonUtils.onInputUpdate(this.that, this.that.movementInput);
    }

    @Overwrite
    public void setPlayerSPHealth(float health)
    {
        if (this.hasValidHealth)
        {
            float f = this.getHealth() - health;

            if (f <= 0.0F)
            {
                this.setHealth(health);

                if (f < 0.0F)
                {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            }
            else
            {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
            }
        }
        else
        {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }
}