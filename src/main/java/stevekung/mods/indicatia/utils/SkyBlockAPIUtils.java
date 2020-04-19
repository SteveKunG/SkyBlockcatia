package stevekung.mods.indicatia.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import stevekung.mods.indicatia.config.ConfigManagerIN;

public class SkyBlockAPIUtils
{
    private static final Gson GSON = new Gson();
    public static int MAX_FAIRY_SOULS;
    private static String API_KEY;
    public static String PLAYER_NAME;
    public static String SKYBLOCK_PROFILE;
    public static String SKYBLOCK_AUCTION;

    public static void setApiKey()
    {
        LoggerIN.info("Setting an API Key");
        SkyBlockAPIUtils.API_KEY = ConfigManagerIN.hypixelApiKey;
        PLAYER_NAME = "https://api.hypixel.net/player?key=" + API_KEY + "&name=";
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/profile?key=" + API_KEY + "&profile=";
        SKYBLOCK_AUCTION = "https://api.hypixel.net/skyblock/auction?key=" + API_KEY + "&profile=";
    }

    public static void setApiKeyFromServer(String uuid)
    {
        ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Hypixel API Key", ConfigManagerIN.hypixelApiKey).set(uuid);
        ConfigManagerIN.hypixelApiKey = uuid;
        ConfigManagerIN.getConfig().save();
        SkyBlockAPIUtils.setApiKey();
    }

    public static void getFairySouls()
    {
        try
        {
            URL url = new URL("https://raw.githubusercontent.com/SteveKunG/Indicatia/1.8.9_skyblock/api/stats_bonuses/misc/max_fairy_souls.json");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            MAX_FAIRY_SOULS = GSON.fromJson(in, MaxFairySouls.class).getMaxFairySouls();
        }
        catch (IOException e)
        {
            MAX_FAIRY_SOULS = 194;
        }
    }

    public static List<ItemStack> decodeItem(JsonObject currentProfile, SkyBlockInventoryType type)
    {
        if (currentProfile.has(type.getApiName()))
        {
            List<ItemStack> itemStack = new ArrayList<>();
            byte[] decode = Base64.getDecoder().decode(currentProfile.get(type.getApiName()).getAsJsonObject().get("data").getAsString().replace("\\u003d", "="));

            try
            {
                NBTTagCompound compound = CompressedStreamTools.readCompressed(new ByteArrayInputStream(decode));
                NBTTagList list = compound.getTagList("i", 10);

                for (int i = type == SkyBlockInventoryType.INVENTORY ? 9 : 0; i < list.tagCount(); ++i)
                {
                    itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
                }
                if (type == SkyBlockInventoryType.INVENTORY)
                {
                    for (int i = 0; i < 9; ++i)
                    {
                        itemStack.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
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