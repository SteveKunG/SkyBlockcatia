package com.stevekung.skyblockcatia.utils.skyblock;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class SBRecipeViewer
{
    public static void viewRecipe(EntityPlayerSP player, Slot slot, int keyCode)
    {
        if (slot.getStack() != null && slot.getStack().hasTagCompound())
        {
            NBTTagCompound extraAttrib = slot.getStack().getTagCompound().getCompoundTag("ExtraAttributes");

            if (extraAttrib.hasKey("id"))
            {
                String itemId = extraAttrib.getString("id");
                player.sendChatMessage("/viewrecipe " + itemId);
            }
        }
    }
}