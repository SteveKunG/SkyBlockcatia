package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.List;

import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.utils.LangUtils;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SBInventoryTabs
{
    public static SBInventoryTabs[] tabArray = new SBInventoryTabs[10];
    public static final SBInventoryTabs INVENTORY = new SBInventoryTabs(0, "inventory", Blocks.chest).setBackgroundImageName("player_inventory");
    public static final SBInventoryTabs ENDER_CHEST = new SBInventoryTabs(1, "ender_chest", Blocks.ender_chest);
    public static final SBInventoryTabs PERSONAL_VAULT = new SBInventoryTabs(2, "personal_vault", RenderUtils.getSkullItemStack("5dc858cb-5ca4-4aef-90eb-091790d2ec0e", "2bdd62f25f4a49cc42e054a3f212c3e0092138299172d7d8f3d438214ca972ac"));
    public static final SBInventoryTabs ACCESSORY = new SBInventoryTabs(3, "accessory", RenderUtils.getSkullItemStack("a97ab432-e9d4-4c42-aff7-2775265b2b4c", "961a918c0c49ba8d053e522cb91abc74689367b4d8aa06bfc1ba9154730985ff"));
    public static final SBInventoryTabs POTION = new SBInventoryTabs(4, "potion", RenderUtils.getSkullItemStack("d01f7f54-635d-40a5-be43-7f322fd05cc6", "9f8b82427b260d0a61e6483fc3b2c35a585851e08a9a9df372548b4168cc817c"));
    public static final SBInventoryTabs FISHING = new SBInventoryTabs(5, "fishing", RenderUtils.getSkullItemStack("49a4ee3b-7918-462b-9040-de9a9b2e9946", "eb8e297df6b8dffcf135dba84ec792d420ad8ecb458d144288572a84603b1631"));
    public static final SBInventoryTabs WARDROBE = new SBInventoryTabs(6, "wardrobe", Items.leather_chestplate)
    {
        @Override
        public ItemStack getIcon()
        {
            ItemStack itemStack = new ItemStack(Items.leather_chestplate);
            ((ItemArmor)itemStack.getItem()).setColor(itemStack, 8339378);
            return itemStack;
        }
    };
    public static final SBInventoryTabs PET = new SBInventoryTabs(7, "pet", Items.bone);
    public static final SBInventoryTabs SACKS = new SBInventoryTabs(8, "sacks", RenderUtils.getSkullItemStack("e2334248-bd14-37cd-8800-088f9aa8ead5", "80a077e248d142772ea800864f8c578b9d36885b29daf836b64a706882b6ec10"));
    public static final SBInventoryTabs QUIVER = new SBInventoryTabs(9, "quiver", RenderUtils.getSkullItemStack("020241bd-6ddb-4358-895c-037c91f4f52d", "4cb3acdc11ca747bf710e59f4c8e9b3d949fdd364c6869831ca878f0763d1787"));
    public static final SBInventoryTabs CANDY = new SBInventoryTabs(10, "candy", RenderUtils.getSkullItemStack("906876f2-55d9-3965-9e57-f5732c765617", "e50f712e877dfd910c97f3819a200a05d49ee6b83b592686e099b9ecd443f228"));
    private final int index;
    private final String label;
    private String texture = "items";
    private boolean hasScrollbar = true;
    private ItemStack icon;

    private SBInventoryTabs(int index, String label, ItemStack icon)
    {
        if (index >= tabArray.length)
        {
            SBInventoryTabs[] tmp = new SBInventoryTabs[index + 1];

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

    private SBInventoryTabs(int index, String label, Item item)
    {
        this(index, label, new ItemStack(item));
    }

    private SBInventoryTabs(int index, String label, Block block)
    {
        this(index, label, new ItemStack(block));
    }

    public int getTabIndex()
    {
        return this.index;
    }

    public SBInventoryTabs setBackgroundImageName(String texture)
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

    public SBInventoryTabs setNoScrollbar()
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
        for (SBInventoryTabs.Data inventory : GuiSkyBlockAPIViewer.SKYBLOCK_INV)
        {
            if (inventory.getTab() == this)
            {
                items.addAll(inventory.getItems());
            }
        }
    }

    public boolean isDisabled()
    {
        for (SBInventoryTabs.Data inventory : GuiSkyBlockAPIViewer.SKYBLOCK_INV)
        {
            if (inventory.getTab() == this)
            {
                List<ItemStack> itemList = inventory.getItems();
                return itemList.isEmpty() || itemList.stream().allMatch(itemStack -> itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.barrier) || itemStack == null);
            }
        }
        return false;
    }

    public static class Data
    {
        private final List<ItemStack> items;
        private final SBInventoryTabs tab;

        public Data(List<ItemStack> items, SBInventoryTabs tab)
        {
            this.items = items;
            this.tab = tab;
        }

        public List<ItemStack> getItems()
        {
            return this.items;
        }

        public SBInventoryTabs getTab()
        {
            return this.tab;
        }
    }

    public static class InventoryExtended extends InventoryBasic
    {
        public InventoryExtended(int slotCount)
        {
            super("tmp", false, slotCount);
        }

        @Override
        public int getInventoryStackLimit()
        {
            return 20160;
        }
    }
}