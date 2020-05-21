package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBItemUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(WallOrFloorItem.class)
public abstract class MixinWallOrFloorItem extends BlockItem
{
    public MixinWallOrFloorItem(Block floorBlock, Block wallBlock, Item.Properties properties)
    {
        super(floorBlock, properties);
    }

    @Inject(method = "getStateForPlacement(Lnet/minecraft/item/BlockItemUseContext;)Lnet/minecraft/block/BlockState;", cancellable = true, at = @At("HEAD"))
    private void getStateForPlacement(BlockItemUseContext context, CallbackInfoReturnable<BlockState> info)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            info.setReturnValue(null);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        return SBItemUtils.getBlockedItem(player.getHeldItem(hand), player, super.onItemRightClick(world, player, hand));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        return SBItemUtils.getBlockedItemResult(context.getItem(), context.getPlayer(), super.onItemUse(context));
    }
}