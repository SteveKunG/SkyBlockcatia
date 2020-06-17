package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.CurlExecutor;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.api.MaxFairySouls;
import com.stevekung.stevekungslib.utils.JsonUtils;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.datafix.fixes.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class SBAPIUtils
{
    private static final Gson GSON = new Gson();
    public static int MAX_FAIRY_SOULS;
    public static SupportedPack PACKS;
    private static String API_KEY;
    public static String PLAYER_NAME;
    public static String SKYBLOCK_PROFILE;
    public static String SKYBLOCK_AUCTION;
    public static String BAZAAR;
    public static String GUILD;

    public static void setApiKey()
    {
        SkyBlockcatiaMod.LOGGER.info("Setting an API Key");
        SBAPIUtils.API_KEY = SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get();
        PLAYER_NAME = "https://api.hypixel.net/player?key=" + API_KEY + "&name=";
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/profile?key=" + API_KEY + "&profile=";
        SKYBLOCK_AUCTION = "https://api.hypixel.net/skyblock/auction?key=" + API_KEY + "&profile=";
        BAZAAR = "https://api.hypixel.net/skyblock/bazaar?key=" + API_KEY;
        GUILD = "https://api.hypixel.net/guild?key=" + API_KEY + "&player=";
    }

    public static void setApiKeyFromServer(String uuid)
    {
        SkyBlockcatiaConfig.GENERAL.hypixelApiKey.set(uuid);
        SBAPIUtils.setApiKey();
    }

    public static void getFairySouls()
    {
        try
        {
            MAX_FAIRY_SOULS = GSON.fromJson(CurlExecutor.execute("api/stats_bonuses/misc/max_fairy_souls.json"), MaxFairySouls.class).getMaxFairySouls();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MAX_FAIRY_SOULS = 194;
        }
    }

    public static void getSupportedPackNames()
    {
        try
        {
            PACKS = GSON.fromJson(CurlExecutor.execute("pack_name.json"), SupportedPack.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static List<ItemStack> decodeItem(JsonObject currentProfile, SBInventoryType type)
    {
        if (currentProfile.has(type.getApiName()))
        {
            List<ItemStack> itemStack = new ArrayList<>();
            byte[] decode = Base64.getDecoder().decode(currentProfile.get(type.getApiName()).getAsJsonObject().get("data").getAsString().replace("\\u003d", "="));

            try
            {
                CompoundNBT compound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(decode));
                ListNBT list = compound.getList("i", Constants.NBT.TAG_COMPOUND);

                for (int i = type == SBInventoryType.INVENTORY ? 9 : 0; i < list.size(); ++i)
                {
                    itemStack.add(SBAPIUtils.flatteningItemStack(list.getCompound(i)));
                }
                if (type == SBInventoryType.INVENTORY)
                {
                    for (int i = 0; i < 9; ++i)
                    {
                        itemStack.add(SBAPIUtils.flatteningItemStack(list.getCompound(i)));
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return itemStack;
        }
        else
        {
            List<ItemStack> itemStack = new ArrayList<>();
            ItemStack barrier = new ItemStack(Blocks.BARRIER);
            barrier.setDisplayName(JsonUtils.create(type.getName() + " is not available!").setStyle(new Style().setItalic(false)).applyTextStyle(TextFormatting.RED));

            for (int i = 0; i < 36; ++i)
            {
                itemStack.add(barrier);
            }
            return itemStack;
        }
    }

    public static ItemStack flatteningItemStack(CompoundNBT apiCompound)
    {
        CompoundNBT newNbt = new CompoundNBT();
        String itemId = "minecraft:air";
        short oldItemId = apiCompound.getShort("id");
        short damage = apiCompound.getShort("Damage");
        CompoundNBT sbTag = apiCompound.getCompound("tag");
        String newItemReg = ItemStackDataFlattening.updateItem(ItemIntIDToString.getItem(oldItemId), damage);

        if (newItemReg != null)
        {
            itemId = EntityRenaming1510.ITEM_RENAME_MAP.getOrDefault(newItemReg, newItemReg);
        }
        else
        {
            try
            {
                String itemId2 = SBAPIUtils.getKey(BlockStateFlatternEntities.MAP, (int)oldItemId);
                String newItemReg2 = ItemStackDataFlattening.updateItem(itemId2, damage);

                if (newItemReg2 != null)
                {
                    itemId = EntityRenaming1510.ITEM_RENAME_MAP.getOrDefault(newItemReg2, newItemReg2);
                }
                else
                {
                    itemId = itemId2;
                }
            }
            catch (NoSuchElementException e)
            {
                itemId = ItemIntIDToString.getItem(oldItemId);
            }

            if (itemId.equals("minecraft:potion"))
            {
                String potionId = PotionItems.POTION_IDS[damage & 127];

                if ((damage & 16384) == 16384)
                {
                    itemId = "minecraft:splash_potion";
                }
                sbTag.putString("Potion", potionId == null ? "minecraft:water" : potionId);
            }
        }

        if (sbTag != null)
        {
            ListNBT loreList = new ListNBT();
            StringNBT name = StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(sbTag.getCompound("display").getString("Name"))));
            ListNBT sbLore = sbTag.getCompound("display").getList("Lore", Constants.NBT.TAG_STRING);

            for (int loreI = 0; loreI < sbLore.size(); loreI++)
            {
                loreList.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(sbLore.getString(loreI)))));
            }

            if (sbTag.contains("ench"))
            {
                ListNBT enchantmentList = sbTag.getList("ench", Constants.NBT.TAG_COMPOUND);

                if (enchantmentList.size() == 0) // dummy enchantment
                {
                    enchantmentList.add(new CompoundNBT());
                }
                else
                {
                    for (int enchI = 0; enchI < enchantmentList.size(); enchI++)
                    {
                        int enchant2 = enchantmentList.getCompound(enchI).getInt("id");
                        enchantmentList.getCompound(enchI).putString("id", ItemStackEnchantmentFix.field_208047_a.getOrDefault(enchant2, "null"));
                    }
                }
                sbTag.put("Enchantments", enchantmentList);
            }

            CompoundNBT sbDisplay = sbTag.getCompound("display");
            sbDisplay.remove("Name");
            sbDisplay.remove("Lore");
            sbDisplay.put("Name", name);
            sbDisplay.put("Lore", loreList);

            sbTag.remove("ench");
            sbTag.put("display", sbDisplay);

            newNbt.putString("id", itemId);
            newNbt.putByte("Count", apiCompound.getByte("Count"));
            newNbt.put("tag", sbTag);
        }
        return ItemStack.read(newNbt);
    }

    private static <K, V> K getKey(Map<K, V> map, V value)
    {
        return map.keySet().stream().filter(key -> value.equals(map.get(key))).findFirst().get();
    }
}