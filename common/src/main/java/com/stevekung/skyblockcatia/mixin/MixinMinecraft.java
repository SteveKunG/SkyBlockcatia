package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    @Redirect(method = "handleKeybinds()V", slice = @Slice(from = @At(value = "FIELD", target = "net/minecraft/client/Options.keyInventory:Lnet/minecraft/client/KeyMapping;"), to = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/MultiPlayerGameMode.isServerControlledInventory()Z")), at = @At(value = "INVOKE", target = "net/minecraft/client/KeyMapping.consumeClick()Z"))
    private boolean disableInventory(KeyMapping key)
    {
        var mc = (Minecraft) (Object) this;
        var foundDragon = false;

        for (var entity : mc.level.entitiesForRendering())
        {
            if (entity instanceof EnderDragon)
            {
                foundDragon = true;
                break;
            }
        }
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.consumeClick() && mc.player.isShiftKeyDown();
        }
        return key.consumeClick();
    }

    @Redirect(method = "handleKeybinds()V", slice = @Slice(from = @At(value = "FIELD", target = "net/minecraft/client/Options.keySwapOffhand:Lnet/minecraft/client/KeyMapping;"), to = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ServerboundPlayerActionPacket")), at = @At(value = "INVOKE", target = "net/minecraft/client/KeyMapping.consumeClick()Z"))
    private boolean disableSwapItem(KeyMapping key)
    {
        return !SkyBlockEventHandler.isSkyBlock && key.consumeClick();
    }
}