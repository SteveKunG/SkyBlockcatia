package stevekung.mods.indicatia.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import stevekung.mods.indicatia.config.EnumEquipment;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.utils.ModDecimalFormat;

public class EquipmentOverlay
{
    private static final List<String> BAIT_LIST = new ArrayList<>(Arrays.asList("WHALE_BAIT", "MINNOW_BAIT", "SPIKED_BAIT", "ICE_BAIT", "FISH_BAIT", "DARK_BAIT", "SPOOKY_BAIT", "BLESSED_BAIT", "LIGHT_BAIT", "CARROT_BAIT"));
    protected static final List<String> FISHING_ROD_LIST = new ArrayList<>(Arrays.asList("YETI_ROD", "THE_SHREDDER", "LEGEND_ROD", "CHAMP_ROD", "AUGER_ROD", "CHALLENGE_ROD", "WINTER_ROD", "FISHING_ROD", "ICE_ROD", "PRISMARINE_ROD", "SPONGE_ROD", "SPEEDSTER_ROD", "FARMER_ROD"));
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
        String status = EnumEquipment.Status.getById(ExtendedConfig.instance.equipmentStatus);

        if (status.equals("none") || this.itemStack.isItemStackDamageable() && (status.equals("count") || status.equals("count_and_stack")))
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

    public String renderBaitInfo()
    {
        int baitCount = EquipmentOverlay.getInventoryFishBaitCount(this.mc.thePlayer.inventory);

        if (this.itemStack.getItem() instanceof ItemFishingRod && this.itemStack.hasTagCompound() && FISHING_ROD_LIST.stream().anyMatch(id -> this.itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals(id)) && baitCount > 0)
        {
            return String.valueOf(baitCount);
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
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
    }

    private static String getArmorDurabilityStatus(ItemStack itemStack)
    {
        String status = EnumEquipment.Status.getById(ExtendedConfig.instance.equipmentStatus);

        switch (status)
        {
        case "damage/max_damage":
        default:
            return itemStack.getMaxDamage() - itemStack.getItemDamage() + "/" + itemStack.getMaxDamage();
        case "percent":
            return EquipmentOverlay.calculateItemDurabilityPercent(itemStack) + "%";
        case "damage":
            return String.valueOf(itemStack.getMaxDamage() - itemStack.getItemDamage());
        case "none":
        case "count":
        case "count_and_stack":
            return "";
        }
    }

    private static int calculateItemDurabilityPercent(ItemStack itemStack)
    {
        return itemStack.getMaxDamage() <= 0 ? 0 : 100 - itemStack.getItemDamage() * 100 / itemStack.getMaxDamage();
    }

    private static String getItemStackCount(ItemStack itemStack, int count)
    {
        String status = EnumEquipment.Status.getById(ExtendedConfig.instance.equipmentStatus);
        double stack = count / (double)itemStack.getMaxStackSize();
        return count == 1 || itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("Unbreakable") ? "" : String.valueOf(status.equals("count_and_stack") ? count + "/" + EquipmentOverlay.STACK.format(stack) : count);
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

    private static int getInventoryFishBaitCount(InventoryPlayer inventory)
    {
        int baitCount = 0;

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack itemStack = inventory.getStackInSlot(i);

            if (itemStack == null || !itemStack.hasTagCompound())
            {
                continue;
            }
            if (itemStack.getItem() == Items.skull && itemStack.getMetadata() == 3 && BAIT_LIST.stream().anyMatch(id -> itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals(id)))
            {
                baitCount += itemStack.stackSize;
            }
        }
        return baitCount;
    }
}