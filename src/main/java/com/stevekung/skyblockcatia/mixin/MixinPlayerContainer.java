package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;

@Mixin(PlayerContainer.class)
public class MixinPlayerContainer
{
    @Redirect(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "net/minecraft/inventory/container/PlayerContainer.addSlot(Lnet/minecraft/inventory/container/Slot;)Lnet/minecraft/inventory/container/Slot;", ordinal = 5))
    private Slot preventShieldSlot(PlayerContainer container, Slot slot, PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn)
    {
        if (Utils.isHypixel() && SkyBlockEventHandler.isSkyBlock)
        {
            return new Slot(playerInventory, 40, 77, 62)
            {
                @Override
                public boolean isEnabled()
                {
                    return false;
                }
            };
        }
        return slot;
    }
}