package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBItemUtils;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem extends Item
{
    private MixinBlockItem()
    {
        super(null);
    }

    @Inject(method = "onItemUse(Lnet/minecraft/item/ItemUseContext;)Lnet/minecraft/util/ActionResultType;", cancellable = true, at = @At("HEAD"))
    private void onItemUse(ItemUseContext context, CallbackInfoReturnable<ActionResultType> info)
    {
        if (SkyBlockEventHandler.isSkyBlock && !context.getItem().isEmpty() && context.getItem().hasTag())
        {
            CompoundNBT extraAttrib = context.getItem().getTag().getCompound("ExtraAttributes");

            if (SBItemUtils.CLICKABLE.stream().anyMatch(id -> extraAttrib.getString("id").equals(id)))
            {
                context.getPlayer().swingArm(Hand.MAIN_HAND);
                info.setReturnValue(ActionResultType.PASS);
            }
        }
    }
}