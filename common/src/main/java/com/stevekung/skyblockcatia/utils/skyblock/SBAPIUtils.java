package com.stevekung.skyblockcatia.utils.skyblock;

import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.PlatformConfig;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

public class SBAPIUtils
{
    public static int MAX_FAIRY_SOULS;
    public static SupportedPack[] PACKS;
    private static String API_KEY;

    public static void setApiKey()
    {
        SkyBlockcatia.LOGGER.info("Setting an API Key");
        SBAPIUtils.API_KEY = PlatformConfig.getApiKey();
    }

    public static void setApiKeyFromServer(String uuid)
    {
        PlatformConfig.setApiKey(uuid);
        SBAPIUtils.setApiKey();
    }

    public static void getMisc()
    {
        var misc = TextComponentUtils.GSON.fromJson(DataUtils.getData("misc.json"), SBMisc.class);
        PACKS = misc.supportedPack();
        MAX_FAIRY_SOULS = misc.maxFairySouls();
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

        APIUrl(String head)
        {
            this(head, "");
        }

        APIUrl(String head, String tail)
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