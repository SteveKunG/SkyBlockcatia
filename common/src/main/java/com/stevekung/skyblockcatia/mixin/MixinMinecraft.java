package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

@Mixin(Minecraft.class)
public class MixinMinecraft
{
    private final Minecraft that = (Minecraft)(Object)this;

    @Redirect(method = "handleKeybinds()V", slice = @Slice(from = @At(value = "FIELD", target = "net/minecraft/client/Options.keyInventory:Lnet/minecraft/client/KeyMapping;"), to = @At(value = "INVOKE", target = "net/minecraft/client/multiplayer/MultiPlayerGameMode.isServerControlledInventory()Z")), at = @At(value = "INVOKE", target = "net/minecraft/client/KeyMapping.consumeClick()Z"))
    private boolean disableInventory(KeyMapping key)
    {
        boolean foundDragon = false;

        for (Entity entity : this.that.level.entitiesForRendering())
        {
            if (entity instanceof EnderDragon)
            {
                foundDragon = true;
                break;
            }
        }
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.consumeClick() && this.that.player.isShiftKeyDown();
        }
        return key.consumeClick();
    }

    @Redirect(method = "handleKeybinds()V", slice = @Slice(from = @At(value = "FIELD", target = "net/minecraft/client/Options.keySwapOffhand:Lnet/minecraft/client/KeyMapping;"), to = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ServerboundPlayerActionPacket")), at = @At(value = "INVOKE", target = "net/minecraft/client/KeyMapping.consumeClick()Z"))
    private boolean disableSwapItem(KeyMapping key)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            return false;
        }
        return key.consumeClick();
    }
}