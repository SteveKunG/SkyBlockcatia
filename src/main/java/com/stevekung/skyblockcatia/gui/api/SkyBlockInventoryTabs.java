package com.stevekung.skyblockcatia.gui.api;

import java.util.List;

import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData.SkyBlockInventory;
import com.stevekung.skyblockcatia.utils.LangUtils;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SkyBlockInventoryTabs
{
    public static SkyBlockInventoryTabs[] tabArray = new SkyBlockInventoryTabs[8];
    public static final SkyBlockInventoryTabs INVENTORY = new SkyBlockInventoryTabs(0, "inventory", Blocks.chest).setBackgroundImageName("player_inventory");
    public static final SkyBlockInventoryTabs ENDER_CHEST = new SkyBlockInventoryTabs(1, "ender_chest", Blocks.ender_chest);
    public static final SkyBlockInventoryTabs ACCESSORY = new SkyBlockInventoryTabs(2, "accessory", Items.emerald);
    public static final SkyBlockInventoryTabs POTION = new SkyBlockInventoryTabs(3, "potion", Items.potionitem);
    public static final SkyBlockInventoryTabs FISHING = new SkyBlockInventoryTabs(4, "fishing", Items.fishing_rod);
    public static final SkyBlockInventoryTabs QUIVER = new SkyBlockInventoryTabs(5, "quiver", Items.arrow);
    public static final SkyBlockInventoryTabs CANDY = new SkyBlockInventoryTabs(6, "candy", RenderUtils.getSkullItemStack("906876f2-55d9-3965-9e57-f5732c765617", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUwZjcxMmU4NzdkZmQ5MTBjOTdmMzgxOWEyMDBhMDVkNDllZTZiODNiNTkyNjg2ZTA5OWI5ZWNkNDQzZjIyOCJ9fX0="));
    public static final SkyBlockInventoryTabs WARDROBE = new SkyBlockInventoryTabs(7, "wardrobe", Items.leather_chestplate);
    public static final SkyBlockInventoryTabs PET = new SkyBlockInventoryTabs(8, "pet", Items.bone);
    private final int index;
    private final String label;
    private String texture = "items";
    private boolean hasScrollbar = true;
    private ItemStack icon;

    private SkyBlockInventoryTabs(int index, String label, ItemStack icon)
    {
        if (index >= tabArray.length)
        {
            SkyBlockInventoryTabs[] tmp = new SkyBlockInventoryTabs[index + 1];

            for (int x = 0; x < tabArray.length; x++)
            {
                tmp[x] = tabArray[x];
            }
            tabArray = tmp;
        }
        this.index = index;
        this.label = label;
        tabArray[index] = this;
        this.icon = icon;
    }

    private SkyBlockInventoryTabs(int index, String label, Item item)
    {
        this(index, label, new ItemStack(item));
    }

    private SkyBlockInventoryTabs(int index, String label, Block block)
    {
        this(index, label, new ItemStack(block));
    }

    public int getTabIndex()
    {
        return this.index;
    }

    public SkyBlockInventoryTabs setBackgroundImageName(String texture)
    {
        this.texture = texture;
        return this;
    }

    public String getTranslatedTabLabel()
    {
        return LangUtils.translate("skyblock_tab." + this.label);
    }

    public ItemStack getIcon()
    {
        return this.icon;
    }

    public String getBackgroundTexture()
    {
        return this.texture + ".png";
    }

    public boolean hasScrollBar()
    {
        return this.hasScrollbar;
    }

    public SkyBlockInventoryTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    public int getTabColumn()
    {
        return this.index % 7;
    }

    public boolean isTabInFirstRow()
    {
        return this.index < 7;
    }

    public void displayAllItems(List<ItemStack> items)
    {
        for (SkyBlockInventory inventory : GuiSkyBlockData.SKYBLOCK_INV)
        {
            if (inventory.getTab() == this)
            {
                items.addAll(inventory.getItems());
            }
        }
    }
}