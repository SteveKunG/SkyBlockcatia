package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

@Mixin(ItemStack.class)
public class ItemStackMixin
{
    @Shadow
    private NBTTagCompound stackTagCompound;

    @Inject(method = "isItemEnchanted()Z", cancellable = true, at = @At("HEAD"))
    private void addSkyblockEnchantTag(CallbackInfoReturnable info)
    {
        if (SkyBlockcatiaSettings.instance.fixSkyblockEnchantTag && this.stackTagCompound != null && this.stackTagCompound.hasKey("ExtraAttributes", Constants.NBT.TAG_COMPOUND) && this.stackTagCompound.getCompoundTag("ExtraAttributes").hasKey("enchantments", Constants.NBT.TAG_COMPOUND) && !this.stackTagCompound.getCompoundTag("ExtraAttributes").getCompoundTag("enchantments").hasNoTags())
        {
            info.setReturnValue(true);
        }
    }
}