package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.InventoryType;
import com.stevekung.skyblockcatia.utils.skyblock.api.SkyblockProfiles;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.datafix.fixes.*;
import net.minecraft.world.item.ItemStack;

public class SBItemUtils
{
    private static final Int2ObjectMap<String> ID_MAP_1_8 = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), obj ->
    {
        obj.put(409, "minecraft:prismarine_shard");
        obj.put(410, "minecraft:prismarine_crystals");
        obj.put(411, "minecraft:rabbit");
        obj.put(412, "minecraft:cooked_rabbit");
        obj.put(413, "minecraft:rabbit_stew");
        obj.put(414, "minecraft:rabbit_foot");
        obj.put(415, "minecraft:rabbit_hide");
        obj.put(416, "minecraft:armor_stand");

        obj.put(423, "minecraft:mutton");
        obj.put(424, "minecraft:cooked_mutton");
        obj.put(427, "minecraft:spruce_door");
        obj.put(428, "minecraft:birch_door");
        obj.put(429, "minecraft:jungle_door");
        obj.put(430, "minecraft:acacia_door");
        obj.put(431, "minecraft:dark_oak_door");
        obj.defaultReturnValue("minecraft:air");
    });

    public static List<ItemStack> decodeItem(SkyblockProfiles.Inventory inventory, InventoryType type)
    {
        if (inventory != null)
        {
            List<ItemStack> itemStack = Lists.newArrayList();
            byte[] decode = Base64.getDecoder().decode(inventory.getData());

            try
            {
                CompoundTag compound = NbtIo.readCompressed(new ByteArrayInputStream(decode));
                ListTag list = compound.getList("i", 10);

                for (int i = 0; i < list.size(); ++i)
                {
                    itemStack.add(SBItemUtils.flatteningItemStack(list.getCompound(i)));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (type == InventoryType.INVENTORY)
            {
                Collections.rotate(itemStack, -9);
            }
            return itemStack;
        }
        return Collections.emptyList();
    }

    public static ItemStack flatteningItemStack(CompoundTag apiCompound)
    {
        CompoundTag newNbt = new CompoundTag();
        String itemId;
        short oldItemId = apiCompound.getShort("id");
        short damage = apiCompound.getShort("Damage");
        CompoundTag sbTag = apiCompound.getCompound("tag");
        String newItemReg = ItemStackTheFlatteningFix.updateItem(oldItemId == 425 ? "minecraft:banner" : ItemIdFix.getItem(oldItemId), damage);

        if (newItemReg != null)
        {
            itemId = EntityTheRenameningFix.RENAMED_ITEMS.getOrDefault(newItemReg, newItemReg);
        }
        else
        {
            try
            {
                String itemId2 = SBItemUtils.getKey(EntityBlockStateFix.MAP, (int) oldItemId);
                String newItemReg2 = ItemStackTheFlatteningFix.updateItem(itemId2, damage);

                if (newItemReg2 != null)
                {
                    itemId = EntityTheRenameningFix.RENAMED_ITEMS.getOrDefault(newItemReg2, newItemReg2);
                }
                else
                {
                    itemId = itemId2;
                }
            }
            catch (NoSuchElementException e)
            {
                itemId = ItemIdFix.getItem(oldItemId);
            }

            if (oldItemId >= 409 && oldItemId <= 416 || oldItemId >= 423 && oldItemId <= 431)
            {
                itemId = ID_MAP_1_8.get(oldItemId);
            }

            if (itemId.equals("minecraft:potion"))
            {
                String potionId = ItemPotionFix.POTIONS[damage & 127];

                if ((damage & 16384) == 16384)
                {
                    itemId = "minecraft:splash_potion";
                }
                sbTag.putString("Potion", potionId == null ? "minecraft:water" : potionId);
            }
            if (itemId.equals("minecraft:spawn_egg") && damage == 0)
            {
                itemId = "minecraft:polar_bear_spawn_egg";
            }
        }

        if (sbTag != null)
        {
            ListTag loreList = new ListTag();
            StringTag name = StringTag.valueOf(TextComponentUtils.toJson(sbTag.getCompound("display").getString("Name")));
            ListTag sbLore = sbTag.getCompound("display").getList("Lore", 8);

            for (int loreI = 0; loreI < sbLore.size(); loreI++)
            {
                loreList.add(StringTag.valueOf(TextComponentUtils.toJson(sbLore.getString(loreI))));
            }

            if (sbTag.contains("SkullOwner"))
            {
                CompoundTag skullOwner = sbTag.getCompound("SkullOwner");
                skullOwner.putIntArray("Id", ItemUtils.uuidToIntArray(skullOwner.getString("Id")));
                sbTag.put("SkullOwner", skullOwner);
            }

            if (sbTag.contains("ench"))
            {
                ListTag enchantmentList = sbTag.getList("ench", 10);

                if (enchantmentList.size() == 0) // dummy enchantment
                {
                    enchantmentList.add(new CompoundTag());
                }
                else
                {
                    for (int enchI = 0; enchI < enchantmentList.size(); enchI++)
                    {
                        int enchant2 = enchantmentList.getCompound(enchI).getInt("id");
                        enchantmentList.getCompound(enchI).putString("id", ItemStackEnchantmentNamesFix.MAP.getOrDefault(enchant2, "null"));
                    }
                }
                sbTag.put("Enchantments", enchantmentList);
            }

            CompoundTag sbDisplay = sbTag.getCompound("display");
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
        return ItemStack.of(newNbt);
    }

    private static <K, V> K getKey(Map<K, V> map, V value)
    {
        return map.keySet().stream().filter(key -> value.equals(map.get(key))).findFirst().get();
    }
}