package com.stevekung.skyblockcatia.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

@Mixin(ItemStack.class)
public class MixinItemStack
{
    @Shadow
    private CompoundNBT tag;

    @Inject(method = "isEnchanted()Z", cancellable = true, at = @At("HEAD"))
    private void addSkyblockEnchantTag(CallbackInfoReturnable info)
    {
        if (SkyBlockcatiaSettings.INSTANCE.fixSkyblockEnchantTag && this.tag != null && this.tag.contains("ExtraAttributes", Constants.NBT.TAG_COMPOUND) && this.tag.getCompound("ExtraAttributes").contains("enchantments", Constants.NBT.TAG_COMPOUND) && !this.tag.getCompound("ExtraAttributes").getCompound("enchantments").isEmpty())
        {
            info.setReturnValue(true);
        }
    }
}