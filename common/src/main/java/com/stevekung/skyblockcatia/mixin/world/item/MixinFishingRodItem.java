package com.stevekung.skyblockcatia.mixin.world.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(FishingRodItem.class)
public class MixinFishingRodItem
{
    @Inject(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0, shift = Shift.BEFORE))
    private void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<ItemStack> info)
    {
        GrapplingHookEvent.GRAPPLING_HOOK.invoker().onHooked(player.getItemInHand(hand));
    }
}