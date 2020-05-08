package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Redirect(method = "processKeyBinds()V", at = @At(value = "INVOKE", target = "net/minecraft/client/settings/KeyBinding.isPressed()Z", ordinal = 3))
    private boolean disableInventory(KeyBinding key)
    {
        boolean foundDragon = false;

        for (Entity entity : this.that.world.getAllEntities())
        {
            if (entity instanceof EnderDragonEntity)
            {
                foundDragon = true;
                break;
            }
        }
        if (SkyBlockEventHandler.isSkyBlock && SBExtendedConfig.INSTANCE.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.isPressed() && this.that.player.isSneaking();
        }
        return key.isPressed();
    }
}