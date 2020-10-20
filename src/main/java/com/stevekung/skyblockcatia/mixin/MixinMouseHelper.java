package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(MouseHelper.class)
public abstract class MixinMouseHelper
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "scrollCallback(JDD)V", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerInventory.changeCurrentItem(D)V"))
    private void changeCurrentItem(PlayerInventory playerInv, double direction)
    {
        boolean foundDragon = false;

        for (Entity entity : this.minecraft.world.getAllEntities())
        {
            if (entity instanceof EnderDragonEntity)
            {
                foundDragon = true;
                break;
            }
        }
        if (!(SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.preventScrollHotbarWhileFightDragon && foundDragon))
        {
            playerInv.changeCurrentItem(direction);
        }
    }
}