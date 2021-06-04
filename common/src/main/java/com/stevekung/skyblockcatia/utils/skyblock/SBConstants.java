package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.stevekung.stevekungslib.utils.ItemUtils;
import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class SBConstants
{
    public static final Map<String, String> SKYBLOCK_ITEM_ID_REMAP = Util.make(Maps.newHashMap(), map ->
    {
        map.put("seeds", "wheat_seeds");
        map.put("raw_chicken", "chicken");
        map.put("carrot_item", "carrot");
        map.put("potato_item", "potato");
        map.put("sulphur", "gunpowder");
        map.put("mushroom_collection", "red_mushroom");
        map.put("sugar_cane", "reeds");
        map.put("pork", "porkchop");
        map.put("nether_stalk", "nether_wart");
        map.put("raw_fish", "fish");
        map.put("ink_sack", "dye");
        map.put("water_lily", "waterlily");
        map.put("ender_stone", "end_stone");
        map.put("log_2", "log2");
        map.put("snow_ball", "snowball");
        map.put("raw_beef", "beef");
    });
    public static final Map<String, String> SBITEM_ID_TO_MC_REMAP = Util.make(Maps.newHashMap(), map -> map.put("minecraft:carved_pumpkin", "minecraft:pumpkin"));
    public static final Map<SBCollections.Type, ImmutableList<ItemLike>> COLLECTION_MAP = Util.make(Maps.newHashMap(), map ->
    {
        map.put(SBCollections.Type.MINING, ImmutableList.of(Blocks.COBBLESTONE, Items.COAL, Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD, Items.REDSTONE, Items.QUARTZ, Blocks.OBSIDIAN, Items.GLOWSTONE_DUST, Blocks.GRAVEL, Blocks.ICE, Blocks.NETHERRACK, Blocks.SAND, Blocks.END_STONE, Items.LAPIS_LAZULI));
        map.put(SBCollections.Type.COMBAT, ImmutableList.of(Items.ROTTEN_FLESH, Items.BONE, Items.STRING, Items.SPIDER_EYE, Items.GUNPOWDER, Items.ENDER_PEARL, Items.GHAST_TEAR, Items.SLIME_BALL, Items.BLAZE_ROD, Items.MAGMA_CREAM));
        map.put(SBCollections.Type.FORAGING, ImmutableList.of(Blocks.OAK_LOG, Blocks.BIRCH_LOG, Blocks.SPRUCE_LOG, Blocks.ACACIA_LOG, Blocks.JUNGLE_LOG, Blocks.DARK_OAK_LOG));
        map.put(SBCollections.Type.FISHING, ImmutableList.of(Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH, Items.PRISMARINE_SHARD, Items.PRISMARINE_CRYSTALS, Items.CLAY_BALL, Blocks.LILY_PAD, Blocks.SPONGE, Items.INK_SAC));
        map.put(SBCollections.Type.FARMING, ImmutableList.of(Items.SUGAR_CANE, Blocks.PUMPKIN, Items.CARROT, Items.WHEAT, Items.POTATO, Items.MELON, Items.COCOA_BEANS, Items.FEATHER, Items.CHICKEN, Items.PORKCHOP, Items.MUTTON, Items.LEATHER, Blocks.RED_MUSHROOM, Items.NETHER_WART, Items.RABBIT, Items.WHEAT_SEEDS, Blocks.CACTUS));
    });
    public static final Map<String, ItemStack> ENCHANTED_ID_TO_ITEM = Util.make(Maps.newHashMap(), map ->
    {
        map.put("enchanted_mithril", new ItemStack(Items.PRISMARINE_CRYSTALS));
        map.put("enchanted_iron", new ItemStack(Items.IRON_INGOT));
        map.put("enchanted_endstone", new ItemStack(Blocks.END_STONE));
        map.put("enchanted_gold", new ItemStack(Items.GOLD_INGOT));
        map.put("enchanted_lapis_lazuli", new ItemStack(Items.LAPIS_LAZULI));
        map.put("enchanted_titanium", ItemUtils.getSkullItemStack("deb23698-94ea-3571-bb89-cd37ba5d15d8", "3dcc0ec9873f4f8d407ba0a0f983e257787772eaf8784e226a61c7f727ac9e26"));
        map.put("enchanted_dark_oak_log", new ItemStack(Blocks.DARK_OAK_LOG));
    });
}