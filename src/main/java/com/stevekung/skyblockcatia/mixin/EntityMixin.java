package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    private final Entity that = (Entity) (Object) this;

    @Overwrite
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        this.that.prevPosX = this.that.posX = x;
        this.that.prevPosY = this.that.posY = y;
        this.that.prevPosZ = this.that.posZ = z;
        this.that.prevRotationYaw = this.that.rotationYaw = yaw;
        this.that.prevRotationPitch = this.that.rotationPitch = pitch;

        this.that.setPosition(this.that.posX, this.that.posY, this.that.posZ);
        this.setRotation(yaw, pitch);
    }

    @Overwrite
    protected void setRotation(float yaw, float pitch)
    {
        this.that.rotationYaw = yaw;
        this.that.rotationPitch = pitch;
    }
}