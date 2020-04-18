package stevekung.mods.indicatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import stevekung.mods.indicatia.handler.GrapplingHookEvent;

@Mixin(ItemFishingRod.class)
public class ItemFishingRodMixin
{
    @Inject(method = "onItemRightClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "net/minecraft/entity/projectile/EntityFishHook.handleHookRetraction()I"))
    private void onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, CallbackInfoReturnable<ItemStack> info)
    {
        MinecraftForge.EVENT_BUS.post(new GrapplingHookEvent(itemStack));
    }
}