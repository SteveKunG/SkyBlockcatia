package stevekung.mods.indicatia.utils;

import net.minecraft.item.ItemStack;

public class SkyBlockCollection
{
    private final ItemStack itemStack;
    private final Type type;
    private final int value;

    public SkyBlockCollection(ItemStack itemStack, Type type, int value)
    {
        this.itemStack = itemStack;
        this.type = type;
        this.value = value;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public Type getCollectionType()
    {
        return this.type;
    }

    public int getValue()
    {
        return this.value;
    }

    public enum ItemId
    {
        SEEDS("wheat_seeds"),
        RAW_CHICKEN("chicken"),
        CARROT_ITEM("carrot"),
        POTATO_ITEM("potato"),
        SULPHUR("gunpowder"),
        MUSHROOM_COLLECTION("red_mushroom"),
        SUGAR_CANE("reeds"),
        PORK("porkchop"),
        NETHER_STALK("nether_wart"),
        RAW_FISH("fish"),
        INK_SACK("dye"),
        WATER_LILY("waterlily"),
        ENDER_STONE("end_stone"),
        LOG_2("log2");

        public static final ItemId[] VALUES = ItemId.values();
        private final String minecraftId;

        private ItemId(String minecraftId)
        {
            this.minecraftId = minecraftId;
        }

        public String getMinecraftId()
        {
            return this.minecraftId;
        }
    }

    public enum Type
    {
        FARMING,
        MINING,
        COMBAT,
        FORAGING,
        FISHING;
    }
}