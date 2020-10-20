package com.stevekung.skyblockcatia.core;

import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.RefreshApiDataCommand;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.ClientEventHandler;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.integration.IndicatiaIntegration;
import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBMinions;
import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.skyblockcatia.utils.skyblock.api.PlayerStatsBonus;
import com.stevekung.stevekungslib.utils.CommonUtils;
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
    public static boolean isIndicatiaLoaded;
    public static final List<String> SUPPORTERS_NAME = new CopyOnWriteArrayList<>();

    static
    {
        ToastLog.setup();

        CommonUtils.runAsync(() ->
        {
            try
            {
                BufferedReader reader = DataGetter.get("SKYBLOCKCATIA_USERNAME");
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

    public SkyBlockcatiaMod()
    {
        CommonUtils.registerConfig(ModConfig.Type.CLIENT, SkyBlockcatiaConfig.GENERAL_BUILDER);
        CommonUtils.registerModEventBus(SkyBlockcatiaConfig.class);
        CommonUtils.addModListener(this::phaseOne);
        CommonUtils.addModListener(this::loadComplete);

        SkyBlockcatiaMod.isSkyblockAddonsLoaded = ModList.get().isLoaded("skyblockaddons");
        SkyBlockcatiaMod.isIndicatiaLoaded = ModList.get().isLoaded("indicatia");
    }

    private void phaseOne(FMLCommonSetupEvent event)
    {
        KeyBindingHandler.init();

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new MainEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new SkyBlockEventHandler());
        CommonUtils.registerEventHandler(new ClientEventHandler());

        if (SkyBlockcatiaMod.isIndicatiaLoaded)
        {
            IndicatiaIntegration.registerHandler();
        }

        ClientCommands.register(new SkyBlockAPIViewerCommand());
        ClientCommands.register(new BazaarViewerCommand());
        ClientCommands.register(new RefreshApiDataCommand());
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        SBAPIUtils.setApiKey();
        CommonUtils.runAsync(SkyBlockcatiaMod::downloadAPIData);
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(MainEventHandler::getBazaarData, 0, 10, TimeUnit.SECONDS);
    }

    public static void downloadAPIData()
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
            ExpProgress.DUNGEON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.DUNGEON);

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
    }
}