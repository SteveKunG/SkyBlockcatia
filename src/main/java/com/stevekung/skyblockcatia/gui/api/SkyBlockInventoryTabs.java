package com.stevekung.skyblockcatia.gui.api;

import java.util.List;

import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData.SkyBlockInventory;
import com.stevekung.skyblockcatia.utils.LangUtils;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SkyBlockInventoryTabs
{
    public static SkyBlockInventoryTabs[] tabArray = new SkyBlockInventoryTabs[10];
    public static final SkyBlockInventoryTabs INVENTORY = new SkyBlockInventoryTabs(0, "inventory", Blocks.chest).setBackgroundImageName("player_inventory");
    public static final SkyBlockInventoryTabs ENDER_CHEST = new SkyBlockInventoryTabs(1, "ender_chest", Blocks.ender_chest);
    public static final SkyBlockInventoryTabs PERSONAL_VAULT = new SkyBlockInventoryTabs(2, "personal_vault", RenderUtils.getSkullItemStack("5dc858cb-5ca4-4aef-90eb-091790d2ec0e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmJkZDYyZjI1ZjRhNDljYzQyZTA1NGEzZjIxMmMzZTAwOTIxMzgyOTkxNzJkN2Q4ZjNkNDM4MjE0Y2E5NzJhYyJ9fX0="));
    public static final SkyBlockInventoryTabs ACCESSORY = new SkyBlockInventoryTabs(3, "accessory", RenderUtils.getSkullItemStack("a97ab432-e9d4-4c42-aff7-2775265b2b4c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYxYTkxOGMwYzQ5YmE4ZDA1M2U1MjJjYjkxYWJjNzQ2ODkzNjdiNGQ4YWEwNmJmYzFiYTkxNTQ3MzA5ODVmZiJ9fX0="));
    public static final SkyBlockInventoryTabs POTION = new SkyBlockInventoryTabs(4, "potion", RenderUtils.getSkullItemStack("d01f7f54-635d-40a5-be43-7f322fd05cc6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY4YjgyNDI3YjI2MGQwYTYxZTY0ODNmYzNiMmMzNWE1ODU4NTFlMDhhOWE5ZGYzNzI1NDhiNDE2OGNjODE3YyJ9fX0="));
    public static final SkyBlockInventoryTabs FISHING = new SkyBlockInventoryTabs(5, "fishing", RenderUtils.getSkullItemStack("49a4ee3b-7918-462b-9040-de9a9b2e9946", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI4ZTI5N2RmNmI4ZGZmY2YxMzVkYmE4NGVjNzkyZDQyMGFkOGVjYjQ1OGQxNDQyODg1NzJhODQ2MDNiMTYzMSJ9fX0="));
    public static final SkyBlockInventoryTabs WARDROBE = new SkyBlockInventoryTabs(6, "wardrobe", Items.leather_chestplate)
    {
        @Override
        public ItemStack getIcon()
        {
            ItemStack itemStack = new ItemStack(Items.leather_chestplate);
            ((ItemArmor)itemStack.getItem()).setColor(itemStack, 8339378);
            return itemStack;
        }
    };
    public static final SkyBlockInventoryTabs PET = new SkyBlockInventoryTabs(7, "pet", Items.bone);
    public static final SkyBlockInventoryTabs SACKS = new SkyBlockInventoryTabs(8, "sacks", RenderUtils.getSkullItemStack("e2334248-bd14-37cd-8800-088f9aa8ead5", "ewogICJ0aW1lc3RhbXAiIDogMTU5MTMxMDU4NTYwOSwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBhMDc3ZTI0OGQxNDI3NzJlYTgwMDg2NGY4YzU3OGI5ZDM2ODg1YjI5ZGFmODM2YjY0YTcwNjg4MmI2ZWMxMCIKICAgIH0KICB9Cn0="));
    public static final SkyBlockInventoryTabs QUIVER = new SkyBlockInventoryTabs(9, "quiver", RenderUtils.getSkullItemStack("020241bd-6ddb-4358-895c-037c91f4f52d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNiM2FjZGMxMWNhNzQ3YmY3MTBlNTlmNGM4ZTliM2Q5NDlmZGQzNjRjNjg2OTgzMWNhODc4ZjA3NjNkMTc4NyJ9fX0="));
    public static final SkyBlockInventoryTabs CANDY = new SkyBlockInventoryTabs(10, "candy", RenderUtils.getSkullItemStack("906876f2-55d9-3965-9e57-f5732c765617", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUwZjcxMmU4NzdkZmQ5MTBjOTdmMzgxOWEyMDBhMDVkNDllZTZiODNiNTkyNjg2ZTA5OWI5ZWNkNDQzZjIyOCJ9fX0="));
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
        String translated = LangUtils.translate("skyblock_tab." + this.label);
        return this.isDisabled() ? EnumChatFormatting.RED + translated + " not available or empty" : translated;
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

    public boolean isDisabled()
    {
        for (SkyBlockInventory inventory : GuiSkyBlockData.SKYBLOCK_INV)
        {
            if (inventory.getTab() == this)
            {
                List<ItemStack> itemList = inventory.getItems();
                return itemList.isEmpty() || itemList.stream().allMatch(itemStack -> itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.barrier) || itemStack == null);
            }
        }
        return false;
    }
}