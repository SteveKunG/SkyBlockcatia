package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.api.MaxFairySouls;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

public class SBAPIUtils
{
    public static int MAX_FAIRY_SOULS;
    public static SupportedPack PACKS;
    private static String API_KEY;

    public static void setApiKey()
    {
        SkyBlockcatiaMod.LOGGER.info("Setting an API Key");
        SBAPIUtils.API_KEY = SkyBlockcatiaConfig.GENERAL.hypixelApiKey.get();
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
            MAX_FAIRY_SOULS = TextComponentUtils.GSON.fromJson(DataGetter.get("api/stats_bonuses/misc/max_fairy_souls.json"), MaxFairySouls.class).getMaxFairySouls();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MAX_FAIRY_SOULS = 220;
        }
    }

    public static void getSupportedPackNames()
    {
        try
        {
            PACKS = TextComponentUtils.GSON.fromJson(DataGetter.get("pack_name.json"), SupportedPack.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public enum APIUrl
    {
        PLAYER_NAME("https://api.hypixel.net/player?key=", "&name="),
        PLAYER_UUID("https://api.hypixel.net/player?key=", "&uuid="),
        SKYBLOCK_PROFILE("https://api.hypixel.net/skyblock/profile?key=", "&profile="),
        SKYBLOCK_PROFILES("https://api.hypixel.net/skyblock/profiles?key=", "&uuid="),
        SKYBLOCK_AUCTION("https://api.hypixel.net/skyblock/auction?key=", "&profile="),
        BAZAAR("https://api.hypixel.net/skyblock/bazaar?key="),
        GUILD("https://api.hypixel.net/guild?key=", "&player="),
        STATUS("https://api.hypixel.net/status?key=", "&uuid=");

        private final String head;
        private final String tail;

        private APIUrl(String head)
        {
            this(head, "");
        }

        private APIUrl(String head, String tail)
        {
            this.head = head;
            this.tail = tail;
        }

        public String getUrl()
        {
            return this.head + SBAPIUtils.API_KEY + this.tail;
        }
    }
}