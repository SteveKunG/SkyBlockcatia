package com.stevekung.skyblockcatia.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.CurlExecutor;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBMinions;
import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.skyblockcatia.utils.skyblock.api.PlayerStatsBonus;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.LoggerBase;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(SkyBlockcatiaMod.MOD_ID)
public class SkyBlockcatiaMod
{
    public static final String MOD_ID = "skyblockcatia";
    public static final LoggerBase LOGGER = new LoggerBase("SkyBlockcatia");

    public static boolean isSkyblockAddonsLoaded;
    public static boolean isIngameAccountSwitcherLoaded;

    public static boolean GITHUB_DOWN;
    public static boolean NO_UUID_MATCHED;
    private static final List<String> HARDCODE_UUID = new ArrayList<>();
    public static final List<String> SUPPORTERS_NAME = new CopyOnWriteArrayList<>();
    private static final List<String> SUPPORTERS_UUID = new ArrayList<>();
    public static UUID CURRENT_UUID;
    private static boolean DEVENV = true;

    static
    {
        HARDCODE_UUID.add("dd436eb4-01e3-4541-bc85-4a899c879304"); // _Okto
        HARDCODE_UUID.add("d362e682-9f61-4a10-8d73-ad540d235fad"); // Lnwdeen
        HARDCODE_UUID.add("5669d719-e494-47f5-9362-0ece491d0875"); // Badify

        SkyBlockcatiaMod.nahee();
        ToastLog.setup();

        if (!DEVENV)
        {
            CommonUtils.runAsync(() ->
            {
                for (String uuid : SUPPORTERS_UUID)
                {
                    try
                    {
                        SUPPORTERS_NAME.add(SkyBlockcatiaMod.getName(uuid));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public SkyBlockcatiaMod()
    {
        CommonUtils.registerConfig(ModConfig.Type.CLIENT, SkyBlockcatiaConfig.GENERAL_BUILDER);
        CommonUtils.registerModEventBus(SkyBlockcatiaConfig.class);
        CommonUtils.addModListener(this::phaseOne);
        CommonUtils.addModListener(this::loadComplete);

        SkyBlockcatiaMod.isSkyblockAddonsLoaded = ModList.get().isLoaded("skyblockaddons");
        SkyBlockcatiaMod.isIngameAccountSwitcherLoaded = ModList.get().isLoaded("IngameAccountSwitcher");
    }

    private void phaseOne(FMLCommonSetupEvent event)
    {
        SkyBlockcatiaMod.CURRENT_UUID = GameProfileUtils.getUUID();
        KeyBindingHandler.init();

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new MainEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new SkyBlockEventHandler());

        ClientCommands.register(new SkyBlockAPIViewerCommand());
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        SBAPIUtils.setApiKey();
        CommonUtils.runAsync(() ->
        {
            try
            {
                ExpProgress.SKILL = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.SKILL);
                ExpProgress.ZOMBIE_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.ZOMBIE_SLAYER);
                ExpProgress.SPIDER_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.SPIDER_SLAYER);
                ExpProgress.WOLF_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.WOLF_SLAYER);
                ExpProgress.RUNECRAFTING = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.RUNECRAFTING);
                ExpProgress.PET_COMMON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_0);
                ExpProgress.PET_UNCOMMON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_1);
                ExpProgress.PET_RARE = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_2);
                ExpProgress.PET_EPIC = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_3);
                ExpProgress.PET_LEGENDARY = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_4);

                for (PlayerStatsBonus.Type type : PlayerStatsBonus.Type.VALUES)
                {
                    PlayerStatsBonus.getBonusFromRemote(type);
                }
                SBAPIUtils.getFairySouls();
                SBMinions.getMinionSlotFromRemote();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
    }

    private static void nahee()
    {
        List<String> uuidList = new ArrayList<>();

        try
        {
            BufferedReader reader = CurlExecutor.execute("SKYBLOCKCATIA_UUID");
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
            {
                uuidList.add(inputLine);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if (!DEVENV)
            {
                SkyBlockcatiaMod.GITHUB_DOWN = true;
            }
        }

        uuidList.addAll(HARDCODE_UUID);
        SUPPORTERS_UUID.addAll(uuidList);

        if (!uuidList.stream().anyMatch(text ->
        {
            return GameProfileUtils.getUUID().toString().equals(text);
        }))
        {
            if (!DEVENV)
            {
                SkyBlockcatiaMod.NO_UUID_MATCHED = true;
            }
        }
    }

    private static String getName(String uuid) throws JsonSyntaxException, IOException
    {
        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names");
        JsonArray array = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
        return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
    }
}