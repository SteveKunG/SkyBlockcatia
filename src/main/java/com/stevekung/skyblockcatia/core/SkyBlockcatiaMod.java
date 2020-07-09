package com.stevekung.skyblockcatia.core;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.RefreshApiDataCommand;
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

    public static boolean isIndicatiaLoaded;

    public static boolean GITHUB_DOWN;
    public static boolean NO_UUID_MATCHED;
    private static final List<String> HARDCODE_UUID = new ArrayList<>();
    public static final List<String> SUPPORTERS_NAME = new CopyOnWriteArrayList<>();
    private static final List<String> SUPPORTERS_UUID = new ArrayList<>();
    public static UUID CURRENT_UUID;
    private static boolean DEVENV = true;

    static
    {
        HARDCODE_UUID.add("84b5eb0f-11d8-464b-881d-4bba203cc77b");
        HARDCODE_UUID.add("e2d72023-34b9-45c2-825b-63ae2d1b2f36");
        HARDCODE_UUID.add("4675476a-46e5-45ee-89a5-010dc02996d9");
        HARDCODE_UUID.add("f1dfdd47-6e03-4c2d-b766-e414c7b77f10");
        HARDCODE_UUID.add("07e864c4-90d6-4c86-8df2-adf98a843e9e");
        HARDCODE_UUID.add("2cd88ad0-89b1-4ca7-907e-78066fe36b08");
        HARDCODE_UUID.add("f81a81c1-92fc-4714-b8ed-f811e6c61550");
        HARDCODE_UUID.add("266513e1-6c83-4d87-bbc5-d3934f9ca329");

        SkyBlockcatiaMod.kuy();
        ToastLog.setup();
        SBAPIUtils.getSupportedPackNames();

        if (!DEVENV)
        {
            CommonUtils.runAsync(() ->
            {
                try
                {
                    BufferedReader reader = CurlExecutor.execute("SKYBLOCKCATIA_USERNAME");
                    String inputLine;

                    while ((inputLine = reader.readLine()) != null)
                    {
                        SUPPORTERS_NAME.add(inputLine);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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

        SkyBlockcatiaMod.isIndicatiaLoaded = ModList.get().isLoaded("indicatia");
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
        ClientCommands.register(new BazaarViewerCommand());
        ClientCommands.register(new RefreshApiDataCommand());
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
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(MainEventHandler::getBazaarData, 0, 10, TimeUnit.SECONDS);
    }

    private static void kuy()
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
}