package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.List;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen.SkyBlockInventory;
import com.stevekung.stevekungslib.utils.LangUtils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;

public class SBInventoryGroup
{
    public static SBInventoryGroup[] GROUPS = new SBInventoryGroup[8];
    public static final SBInventoryGroup INVENTORY = new SBInventoryGroup(0, "inventory", Blocks.CHEST).setBackgroundImageName("player_inventory");
    public static final SBInventoryGroup ENDER_CHEST = new SBInventoryGroup(1, "ender_chest", Blocks.ENDER_CHEST);
    public static final SBInventoryGroup ACCESSORY = new SBInventoryGroup(2, "accessory", Items.EMERALD);
    public static final SBInventoryGroup POTION = new SBInventoryGroup(3, "potion", PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER));
    public static final SBInventoryGroup FISHING = new SBInventoryGroup(4, "fishing", Items.FISHING_ROD);
    public static final SBInventoryGroup QUIVER = new SBInventoryGroup(5, "quiver", Items.ARROW);
    public static final SBInventoryGroup CANDY = new SBInventoryGroup(6, "candy", SBRenderUtils.getSkullItemStack("906876f2-55d9-3965-9e57-f5732c765617", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUwZjcxMmU4NzdkZmQ5MTBjOTdmMzgxOWEyMDBhMDVkNDllZTZiODNiNTkyNjg2ZTA5OWI5ZWNkNDQzZjIyOCJ9fX0="));
    public static final SBInventoryGroup PET = new SBInventoryGroup(7, "pet", Items.BONE);
    private final int index;
    private final String label;
    private String texture = "items";
    private boolean hasScrollbar = true;
    private ItemStack icon;

    private SBInventoryGroup(int index, String label, Item item)
    {
        this(index, label, new ItemStack(item));
    }

    private SBInventoryGroup(int index, String label, Block block)
    {
        this(index, label, new ItemStack(block));
    }

    private SBInventoryGroup(int index, String label, ItemStack icon)
    {
        this.index = SBInventoryGroup.addGroupSafe(index, this);
        this.label = label;
        this.icon = icon;
    }

    public int getIndex()
    {
        return this.index;
    }

    public SBInventoryGroup setBackgroundImageName(String texture)
    {
        this.texture = texture;
        return this;
    }

    public String getTranslationKey()
    {
        return LangUtils.translate("skyblock_group." + this.label);
    }

    public ItemStack getIcon()
    {
        return this.icon;
    }

    public String getBackgroundTexture()
    {
        return this.texture + ".png";
    }

    public boolean hasScrollbar()
    {
        return this.hasScrollbar;
    }

    public SBInventoryGroup setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    public int getColumn()
    {
        return this.index % 7;
    }

    public boolean isOnTopRow()
    {
        return this.index < 7;
    }

    public void fill(List<ItemStack> items)
    {
        for (SkyBlockInventory inventory : SkyBlockAPIViewerScreen.SKYBLOCK_INV)
        {
            if (inventory.getGroup() == this)
            {
                items.addAll(inventory.getItems());
            }
        }
    }

    private static synchronized int addGroupSafe(int index, SBInventoryGroup newGroup)
    {
        if (index == -1)
        {
            index = GROUPS.length;
        }
        if (index >= GROUPS.length)
        {
            SBInventoryGroup[] tmp = new SBInventoryGroup[index + 1];
            System.arraycopy(GROUPS, 0, tmp, 0, GROUPS.length);
            GROUPS = tmp;
        }
        GROUPS[index] = newGroup;
        return index;
    }
}