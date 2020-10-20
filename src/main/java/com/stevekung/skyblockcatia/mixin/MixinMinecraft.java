package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Inject(method = "run()V", at = @At("HEAD"))
    private void run(CallbackInfo info)
    {
        SBAPIUtils.getSupportedPackNames();
    }

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
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.isPressed() && this.that.player.isSneaking();
        }
        return key.isPressed();
    }

    @Redirect(method = "processKeyBinds()V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/player/ClientPlayerEntity.sendHorseInventory()V"))
    private void openPlayerInventory(ClientPlayerEntity player)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            this.that.displayGuiScreen(new InventoryScreen(player));
        }
    }
}