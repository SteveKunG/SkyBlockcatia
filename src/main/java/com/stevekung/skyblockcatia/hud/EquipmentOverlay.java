package com.stevekung.skyblockcatia.hud;

import com.stevekung.skyblockcatia.config.Equipments;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.ModDecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class EquipmentOverlay
{
    private static final ModDecimalFormat STACK = new ModDecimalFormat("#.##");
    protected final ItemStack itemStack;
    protected final Minecraft mc;

    public EquipmentOverlay(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        this.mc = Minecraft.getMinecraft();
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public String renderInfo()
    {
        Equipments.Status status = Equipments.Status.byId(SkyBlockcatiaSettings.INSTANCE.equipmentStatus);

        if (status == Equipments.Status.NONE || this.itemStack.isItemStackDamageable() && (status == Equipments.Status.COUNT || status == Equipments.Status.COUNT_AND_STACK))
        {
            return "";
        }

        int itemCount = EquipmentOverlay.getInventoryItemCount(this.mc.thePlayer.inventory, this.itemStack);

        if (this.itemStack.isItemStackDamageable())
        {
            return EquipmentOverlay.getArmorDurabilityStatus(this.itemStack);
        }
        else
        {
            return EquipmentOverlay.getItemStackCount(this.itemStack, itemCount);
        }
    }

    public String renderArrowInfo()
    {
        int arrowCount = EquipmentOverlay.getInventoryArrowCount(this.mc.thePlayer.inventory);

        if (this.itemStack.getItem() instanceof ItemBow && arrowCount > 0)
        {
            return String.valueOf(arrowCount);
        }
        return "";
    }

    public static void renderItem(ItemStack itemStack, int x, int y)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
    }

    private static String getArmorDurabilityStatus(ItemStack itemStack)
    {
        Equipments.Status status = Equipments.Status.byId(SkyBlockcatiaSettings.INSTANCE.equipmentStatus);

        switch (status)
        {
        case DAMAGE_AND_MAX_DAMAGE:
        default:
            return itemStack.getMaxDamage() - itemStack.getItemDamage() + "/" + itemStack.getMaxDamage();
        case PERCENT:
            return EquipmentOverlay.calculateItemDurabilityPercent(itemStack) + "%";
        case ONLY_DAMAGE:
            return String.valueOf(itemStack.getMaxDamage() - itemStack.getItemDamage());
        case NONE:
        case COUNT:
        case COUNT_AND_STACK:
            return "";
        }
    }

    private static int calculateItemDurabilityPercent(ItemStack itemStack)
    {
        return itemStack.getMaxDamage() <= 0 ? 0 : 100 - itemStack.getItemDamage() * 100 / itemStack.getMaxDamage();
    }

    private static String getItemStackCount(ItemStack itemStack, int count)
    {
        Equipments.Status status = Equipments.Status.byId(SkyBlockcatiaSettings.INSTANCE.equipmentStatus);
        double stack = count / (double)itemStack.getMaxStackSize();
        return count == 1 || itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("Unbreakable") ? "" : String.valueOf(status == Equipments.Status.COUNT_AND_STACK ? count + "/" + EquipmentOverlay.STACK.format(stack) : count);
    }

    private static int getInventoryItemCount(InventoryPlayer inventory, ItemStack other)
    {
        int count = 0;

        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack playerItems = inventory.getStackInSlot(i);

            if (playerItems == null)
            {
                continue;
            }
            if (playerItems.getItem() == other.getItem() && ItemStack.areItemStackTagsEqual(playerItems, other))
            {
                count += playerItems.stackSize;
            }
        }
        return count;
    }

    private static int getInventoryArrowCount(InventoryPlayer inventory)
    {
        int arrowCount = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack itemStack = inventory.getStackInSlot(i);

            if (itemStack == null)
            {
                continue;
            }
            if (itemStack.getItem() == Items.arrow)
            {
                arrowCount += itemStack.stackSize;
            }
        }
        return arrowCount;
    }
}