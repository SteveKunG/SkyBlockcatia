package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.List;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class SBInventoryGroup
{
    public static SBInventoryGroup[] GROUPS = new SBInventoryGroup[11];
    public static final SBInventoryGroup INVENTORY = new SBInventoryGroup(0, "inventory", Blocks.CHEST).setBackgroundImageName("player_inventory");
    public static final SBInventoryGroup ENDER_CHEST = new SBInventoryGroup(1, "ender_chest", Blocks.ENDER_CHEST);
    public static final SBInventoryGroup BACKPACK = new SBInventoryGroup(2, "backpack", ItemUtils.getSkullItemStack("eef3ea1b-d634-3067-ab58-16040aa7ca88", "439bed42aad5d87ecf16e546a29714de409feec6c15d1f745c35c104639f4d49"));
    public static final SBInventoryGroup PERSONAL_VAULT = new SBInventoryGroup(3, "personal_vault", ItemUtils.getSkullItemStack("5dc858cb-5ca4-4aef-90eb-091790d2ec0e", "2bdd62f25f4a49cc42e054a3f212c3e0092138299172d7d8f3d438214ca972ac"));
    public static final SBInventoryGroup ACCESSORY = new SBInventoryGroup(4, "accessory", ItemUtils.getSkullItemStack("a97ab432-e9d4-4c42-aff7-2775265b2b4c", "961a918c0c49ba8d053e522cb91abc74689367b4d8aa06bfc1ba9154730985ff"));
    public static final SBInventoryGroup POTION = new SBInventoryGroup(5, "potion", ItemUtils.getSkullItemStack("d01f7f54-635d-40a5-be43-7f322fd05cc6", "9f8b82427b260d0a61e6483fc3b2c35a585851e08a9a9df372548b4168cc817c"));
    public static final SBInventoryGroup FISHING = new SBInventoryGroup(6, "fishing", ItemUtils.getSkullItemStack("49a4ee3b-7918-462b-9040-de9a9b2e9946", "eb8e297df6b8dffcf135dba84ec792d420ad8ecb458d144288572a84603b1631"));
    public static final SBInventoryGroup WARDROBE = new SBInventoryGroup(7, "wardrobe", Items.LEATHER_CHESTPLATE)
    {
        @Override
        public ItemStack getIcon()
        {
            ItemStack itemStack = new ItemStack(Items.LEATHER_CHESTPLATE);
            ((DyeableArmorItem) itemStack.getItem()).setColor(itemStack, 8339378);
            return itemStack;
        }
    };
    public static final SBInventoryGroup PET = new SBInventoryGroup(8, "pet", Items.BONE);
    public static final SBInventoryGroup SACKS = new SBInventoryGroup(9, "sacks", ItemUtils.getSkullItemStack("e2334248-bd14-37cd-8800-088f9aa8ead5", "80a077e248d142772ea800864f8c578b9d36885b29daf836b64a706882b6ec10"));
    public static final SBInventoryGroup QUIVER = new SBInventoryGroup(10, "quiver", ItemUtils.getSkullItemStack("020241bd-6ddb-4358-895c-037c91f4f52d", "4cb3acdc11ca747bf710e59f4c8e9b3d949fdd364c6869831ca878f0763d1787"));
    public static final SBInventoryGroup CANDY = new SBInventoryGroup(11, "candy", ItemUtils.getSkullItemStack("906876f2-55d9-3965-9e57-f5732c765617", "e50f712e877dfd910c97f3819a200a05d49ee6b83b592686e099b9ecd443f228"));

    private final int index;
    private final String label;
    private String texture = "items";
    private final ItemStack icon;

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

    public Component getTranslationKey()
    {
        Component translated = LangUtils.translate("skyblock_group." + this.label);
        return this.isDisabled() ? translated.copy().append(" not available or empty").withStyle(ChatFormatting.RED) : translated;
    }

    public ItemStack getIcon()
    {
        return this.icon;
    }

    public String getBackgroundTexture()
    {
        return this.texture + ".png";
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
        for (SBInventoryGroup.Data inventory : SkyBlockAPIViewerScreen.SKYBLOCK_INV)
        {
            if (inventory.getGroup() == this)
            {
                items.addAll(inventory.getItems());
            }
        }
    }

    public boolean isDisabled()
    {
        for (SBInventoryGroup.Data inventory : SkyBlockAPIViewerScreen.SKYBLOCK_INV)
        {
            if (inventory.getGroup() == this)
            {
                List<ItemStack> itemList = inventory.getItems();
                return itemList.isEmpty() || itemList.stream().allMatch(ItemStack::isEmpty);
            }
        }
        return false;
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

    public static class Data
    {
        private final List<ItemStack> items;
        private final SBInventoryGroup group;

        public Data(List<ItemStack> items, SBInventoryGroup group)
        {
            this.items = items;
            this.group = group;
        }

        public List<ItemStack> getItems()
        {
            return this.items;
        }

        public SBInventoryGroup getGroup()
        {
            return this.group;
        }
    }

    public static class ExtendedInventory extends SimpleContainer
    {
        public ExtendedInventory(int slotCount)
        {
            super(slotCount);
        }

        @Override
        public int getMaxStackSize()
        {
            return Integer.MAX_VALUE;
        }
    }
}