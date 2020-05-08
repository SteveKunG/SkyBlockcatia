package com.stevekung.skyblockcatia.utils;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;

public class SkyBlockRecipeViewer
{
    public static void viewRecipe(ClientPlayerEntity player, Slot slot)
    {
        if (!slot.getStack().isEmpty() && slot.getStack().hasTag())
        {
            CompoundNBT extraAttrib = slot.getStack().getTag().getCompound("ExtraAttributes");

            if (extraAttrib.contains("id"))
            {
                String itemId = extraAttrib.getString("id");
                player.sendChatMessage("/viewrecipe " + itemId);
            }
        }
    }
}