package com.stevekung.skyblockcatia.core;

import java.io.BufferedReader;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockcatiaCommand;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.event.handler.ToastTestEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.skyblockcatia.utils.skyblock.api.PlayerStatsBonus;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.LoggerBase;
import com.stevekung.stevekungslib.utils.ModVersionChecker;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

@Mod(SkyBlockcatiaMod.MOD_ID)
public class SkyBlockcatiaMod
{
    public static final String MOD_ID = "skyblockcatia";
    public static final LoggerBase LOGGER = new LoggerBase("SkyBlockcatia");
    public static final List<String> SUPPORTERS_NAME = Lists.newCopyOnWriteArrayList();
    public static final ModVersionChecker CHECKER = new ModVersionChecker(MOD_ID);

    static
    {
        ToastLog.setup();

        CommonUtils.runAsync(() ->
        {
            try
            {
                BufferedReader reader = DataUtils.getData("supporters_username");
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
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true));

        CommonUtils.registerModEventBus(SkyBlockcatiaConfig.class);
        CommonUtils.addModListener(this::phaseOne);
        CommonUtils.addModListener(this::loadComplete);
        CompatibilityUtils.init();
    }

    private void phaseOne(FMLCommonSetupEvent event)
    {
        KeyBindingHandler.init();

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new MainEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new SkyBlockEventHandler());
        CommonUtils.registerEventHandler(new ToastTestEventHandler());

        ClientCommands.register(new SkyBlockAPIViewerCommand());
        ClientCommands.register(new BazaarViewerCommand());
        ClientCommands.register(new SkyBlockcatiaCommand());
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatiaMod.CHECKER.startCheck();
        CommonUtils.runAsync(SkyBlockcatiaMod::downloadAPIData);
    }

    private static void downloadAPIData()
    {
        try
        {
            ExpProgress.DUNGEON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.DUNGEON);

            for (PlayerStatsBonus.Type type : PlayerStatsBonus.Type.VALUES)
            {
                PlayerStatsBonus.getBonusFromRemote(type);
            }

            SBMinions.getMinions();
            SBPets.getPets();
            SBSlayers.getSlayers();
            SBSkills.getSkills();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}