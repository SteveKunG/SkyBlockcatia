package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Inject(method = "run()V", at = @At("HEAD"))
    private void run(CallbackInfo info)
    {
        SBAPIUtils.getMisc();
    }

    @Redirect(method = "processKeyBinds()V", slice = @Slice(
            from = @At(value = "FIELD", target = "net/minecraft/client/GameSettings.keyBindInventory:Lnet/minecraft/client/settings/KeyBinding;"),
            to = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/PlayerController.isRidingHorse()Z")),
            at = @At(value = "INVOKE", target = "net/minecraft/client/settings/KeyBinding.isPressed()Z"))
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

    @Redirect(method = "processKeyBinds()V", slice = @Slice(
            from = @At(value = "FIELD", target = "net/minecraft/client/GameSettings.keyBindSwapHands:Lnet/minecraft/client/settings/KeyBinding;"),
            to = @At(value = "NEW", target = "net/minecraft/network/play/client/CPlayerDiggingPacket")),
            at = @At(value = "INVOKE", target = "net/minecraft/client/settings/KeyBinding.isPressed()Z"))
    private boolean disableSwapItem(KeyBinding key)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.isPressed();
    }
}