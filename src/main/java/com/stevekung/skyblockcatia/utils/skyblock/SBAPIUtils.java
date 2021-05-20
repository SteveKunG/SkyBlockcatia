package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.LoggerIN;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.api.InventoryType;
import com.stevekung.skyblockcatia.utils.skyblock.api.SkyblockProfiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class SBAPIUtils
{
    private static final Gson GSON = new Gson();
    public static int MAX_FAIRY_SOULS;
    public static SupportedPack[] PACKS;
    private static String API_KEY;
    public static String PLAYER_NAME;
    public static String PLAYER_UUID;
    public static String SKYBLOCK_PROFILE;
    public static String SKYBLOCK_PROFILES;
    public static String SKYBLOCK_AUCTION;
    public static String BAZAAR;
    public static String GUILD;

    public static void setApiKey()
    {
        LoggerIN.info("Setting an API Key");
        SBAPIUtils.API_KEY = SkyBlockcatiaConfig.hypixelApiKey;
        PLAYER_NAME = "https://api.hypixel.net/player?key=" + API_KEY + "&name=";
        PLAYER_UUID = "https://api.hypixel.net/player?key=" + API_KEY + "&uuid=";
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/profile?key=" + API_KEY + "&profile=";
        SKYBLOCK_PROFILES = "https://api.hypixel.net/skyblock/profiles?key=" + API_KEY + "&uuid=";
        SKYBLOCK_AUCTION = "https://api.hypixel.net/skyblock/auction?key=" + API_KEY + "&profile=";
        BAZAAR = "https://api.hypixel.net/skyblock/bazaar?key=" + API_KEY;
        GUILD = "https://api.hypixel.net/guild?key=" + API_KEY + "&player=";
    }

    public static void setApiKeyFromServer(String uuid)
    {
        SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Hypixel API Key", SkyBlockcatiaConfig.hypixelApiKey).set(uuid);
        SkyBlockcatiaConfig.hypixelApiKey = uuid;
        SkyBlockcatiaConfig.getConfig().save();
        SBAPIUtils.setApiKey();
    }

    public static void getMisc()
    {
        SBMisc misc = GSON.fromJson(DataUtils.getData("misc.json"), SBMisc.class);
        PACKS = misc.getSupportedPack();
        MAX_FAIRY_SOULS = misc.getMaxFairySouls();
    }

    public static List<ItemStack> decodeItem(SkyblockProfiles.Inventory inventory, InventoryType type)
    {
        if (inventory != null)
        {
            List<ItemStack> itemStack = new ArrayList<>();
            byte[] decode = Base64.getDecoder().decode(inventory.getData());

            try
            {
                NBTTagCompound compound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(decode));
                NBTTagList list = compound.getTagList("i", 10);

                for (int i = 0; i < list.tagCount(); ++i)
                {
                    itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
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
        else
        {
            List<ItemStack> itemStack = new ArrayList<>();
            ItemStack barrier = new ItemStack(Blocks.barrier);
            barrier.setStackDisplayName(EnumChatFormatting.RESET + "" + EnumChatFormatting.RED + type.getName() + " is not available!");

            for (int i = 0; i < 36; ++i)
            {
                itemStack.add(barrier);
            }
            return itemStack;
        }
    }
}